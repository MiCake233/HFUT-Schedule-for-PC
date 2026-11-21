package com.hfut.schedule.desktop.platform

import com.hfut.schedule.logic.util.storage.kv.SecretStore
import java.security.SecureRandom
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

/**
 * macOS 凭据加密：主密钥放在登录钥匙串，磁盘上的值是 AES-GCM 密文。
 *
 * 钥匙串项只有当前用户能读，拷到别的账号或别的机器解不开，效果对齐 Windows DPAPI。
 * 新建主密钥时把口令写到 `security -i` 的标准输入，不放进进程参数。
 * 不在 macOS 上时 [install] 直接返回 false。桌面发行版会因此拒绝明文写入。
 */
internal object MacSecrets {

    private const val ACCOUNT = "HFUT-Schedule"
    private const val SERVICE = "HFUT-Schedule-MasterKey"

    internal data class CommandResult(val code: Int, val output: String)

    internal data class KeychainAdd(val args: List<String>, val stdin: String)

    /**
     * 新建钥匙串项的调用方式。参数里只有 `security -i`，主密钥在标准输入的命令行里。
     */
    internal fun keychainAddInvocation(encodedKey: String): KeychainAdd = KeychainAdd(
        args = listOf("security", "-i"),
        stdin = "add-generic-password -a $ACCOUNT -s $SERVICE -w $encodedKey -U\n",
    )

    /** 测试可以换掉，避免真的调用 `security`。第二参是可选的标准输入。 */
    internal var runner: (List<String>, String?) -> CommandResult = { args, stdin ->
        val process = ProcessBuilder(args).redirectErrorStream(true).start()
        if (stdin != null) {
            process.outputStream.bufferedWriter().use { it.write(stdin) }
        } else {
            process.outputStream.close()
        }
        val text = process.inputStream.bufferedReader().use { it.readText() }
        CommandResult(process.waitFor(), text)
    }

    fun install(): Boolean {
        if (currentDesktopOs() != DesktopOs.MACOS) return false
        val key = readOrCreateKey() ?: return false
        val probe = "mac-secret-" + System.nanoTime()
        val encoded = AesGcmSecret.encrypt(probe, key)
        if (AesGcmSecret.decrypt(encoded, key) != probe) return false
        SecretStore.protector = { plain -> runCatching { AesGcmSecret.encrypt(plain, key) }.getOrNull() }
        SecretStore.unprotector = { body -> runCatching { AesGcmSecret.decrypt(body, key) }.getOrNull() }
        return true
    }

    internal fun readOrCreateKey(): ByteArray? {
        val found = runner(
            listOf("security", "find-generic-password", "-a", ACCOUNT, "-s", SERVICE, "-w"),
            null,
        )
        val existing = found.output.trim().takeIf { found.code == 0 && it.isNotEmpty() }
        val encoded = existing ?: run {
            val created = Base64.getEncoder().encodeToString(SecureRandom().generateSeed(32))
            val add = keychainAddInvocation(created)
            val added = runner(add.args, add.stdin)
            if (added.code != 0) return null
            created
        }
        return decodeKey(encoded)
    }

    internal fun decodeKey(encoded: String): ByteArray? =
        runCatching { Base64.getDecoder().decode(encoded.trim()) }.getOrNull()?.takeIf { it.size == 32 }
}

internal object AesGcmSecret {
    private const val IV_LENGTH = 12
    private const val TAG_BITS = 128

    fun encrypt(plain: String, key: ByteArray): String {
        val iv = ByteArray(IV_LENGTH).also { SecureRandom().nextBytes(it) }
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, SecretKeySpec(key, "AES"), GCMParameterSpec(TAG_BITS, iv))
        val body = cipher.doFinal(plain.toByteArray(Charsets.UTF_8))
        return Base64.getEncoder().encodeToString(iv + body)
    }

    fun decrypt(encoded: String, key: ByteArray): String? = runCatching {
        val raw = Base64.getDecoder().decode(encoded)
        if (raw.size <= IV_LENGTH) return null
        val iv = raw.copyOfRange(0, IV_LENGTH)
        val body = raw.copyOfRange(IV_LENGTH, raw.size)
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.DECRYPT_MODE, SecretKeySpec(key, "AES"), GCMParameterSpec(TAG_BITS, iv))
        String(cipher.doFinal(body), Charsets.UTF_8)
    }.getOrNull()
}
