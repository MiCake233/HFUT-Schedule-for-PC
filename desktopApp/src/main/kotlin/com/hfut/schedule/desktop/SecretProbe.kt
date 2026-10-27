package com.hfut.schedule.desktop

import com.hfut.schedule.logic.util.storage.kv.PlatformSharedPreferences
import com.hfut.schedule.logic.util.storage.kv.SecretStore
import com.hfut.schedule.logic.util.storage.kv.SharedPrefs
import java.io.File

/**
 * 凭据加密自检（`--verify-secret`）。
 *
 * 检查四件事：
 * 1. DPAPI 可用，且能正确往返；
 * 2. 密文被篡改后解不开（返回 null，而不是抛异常）；
 * 3. 敏感键经 [PlatformSharedPreferences] 落盘后，**文件里找不到明文**，读回仍是明文；
 * 4. 真实数据目录里的凭据已经全部是密文。
 *
 * 结果同时写到 `%TEMP%\hfut-secret-probe.txt`：安装版是窗口程序，控制台看不到输出。
 */
fun runSecretProbe(): Boolean {
    val lines = mutableListOf<String>()
    fun emit(line: String) {
        lines.add(line)
        println(line)
    }

    var allOk = true

    val available = Dpapi.isAvailable()
    emit("DPAPI_AVAILABLE=$available")
    if (!available) allOk = false

    if (available) {
        val sample = "TGC=TGT-123456-abcdef-中文-凭据"
        val roundTrip = Dpapi.roundTrip(sample)
        emit("ROUND_TRIP=$roundTrip")
        if (!roundTrip) allOk = false

        // 篡改后的密文必须解不开
        val tmp = File(System.getProperty("java.io.tmpdir"), "hfut-secret-probe.properties").apply { delete() }
        val store = PlatformSharedPreferences(tmp)
        store.edit { putString("TGC", sample) }
        store.edit { putString("Username", "2026212855") }

        val raw = runCatching { tmp.readText() }.getOrDefault("")
        val noPlaintext = !raw.contains(sample) && !raw.contains("TGT-123456")
        emit("ON_DISK_PLAINTEXT_ABSENT=$noPlaintext")
        if (!noPlaintext) allOk = false

        val encoded = SecretStore.isEncoded(
            runCatching { store.all()["s|TGC"] }.getOrNull()
        )
        emit("ON_DISK_ENCODED=$encoded")
        if (!encoded) allOk = false

        val decoded = runCatching { store.getString("TGC", "") }.getOrNull()
        emit("DECODE_ROUND_TRIP=" + (decoded == sample))
        if (decoded != sample) allOk = false

        // 普通键不应被加密（否则可读性全没了）
        val plainKey = runCatching { store.all()["s|Username"] }.getOrNull()
        emit("PLAIN_KEY_UNTOUCHED=" + (plainKey == "2026212855"))
        if (plainKey != "2026212855") allOk = false

        // 篡改：把密文里的 base64 改一个字符
        val tampered = (runCatching { store.all()["s|TGC"] }.getOrNull().orEmpty())
            .let { if (it.length > 24) it.dropLast(6) + "AAAAA=" else it }
        store.edit { putString("TGC", tampered) }
        val tamperDecoded = runCatching { store.getString("TGC", "") }.getOrNull()
        emit("TAMPER_REJECTED=" + (tamperDecoded.isNullOrEmpty()))
        if (!tamperDecoded.isNullOrEmpty()) allOk = false
        tmp.delete()
    }

    // 真实数据目录：统计凭据是否都已加密
    val prefs = runCatching { SharedPrefs.prefs.all() }.getOrDefault(emptyMap())
    val sensitive = SecretStore.SENSITIVE_KEYS.mapNotNull { key ->
        val value = prefs["s|$key"]?.takeIf { it.isNotEmpty() }
        if (value == null) null else key to SecretStore.isEncoded(value)
    }
    emit("LIVE_SENSITIVE_KEYS=" + sensitive.joinToString(",") { it.first + "=" + (if (it.second) "enc" else "PLAIN") })
    val plainLive = sensitive.filterNot { it.second }.map { it.first }
    emit("LIVE_PLAINTEXT_COUNT=${plainLive.size}")
    if (plainLive.isNotEmpty()) {
        emit("LIVE_PLAINTEXT_KEYS=" + plainLive.joinToString(","))
        allOk = false
    }

    // DataStore 里的密码 / JWT / cookie 同样必须是密文
    val datastore = runCatching {
        kotlinx.coroutines.runBlocking {
            com.hfut.schedule.logic.util.storage.kv.DataStoreManager.sensitiveValuesEncoded()
        }
    }.getOrDefault(emptyMap())
    val datastorePlain = datastore.filterValues { !it }.keys
    emit("DATASTORE_SECRETS=" + datastore.entries.joinToString(",") { it.key + "=" + (if (it.value) "enc" else "PLAIN") })
    emit("DATASTORE_PLAINTEXT_COUNT=${datastorePlain.size}")
    if (datastorePlain.isNotEmpty()) allOk = false

    emit("SECRET_OK=$allOk")
    runCatching {
        File(System.getProperty("java.io.tmpdir"), "hfut-secret-probe.txt")
            .writeText(lines.joinToString(System.lineSeparator()))
    }
    return allOk
}
