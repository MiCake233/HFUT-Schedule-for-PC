package com.hfut.schedule.logic.util.storage.kv

/**
 * 敏感键值的落盘加密挂钩。
 *
 * `PlatformSharedPreferences` 把这几类键（登录凭据、票据、token）交给 [protector] 之后再写入
 * properties 文件，读取时用 [unprotector] 还原。这样**上层 150+ 处读写代码一行都不用改**，
 * 而磁盘上不再有明文密码 / TGC / SESSION。
 *
 * 具体加密由各端在启动时注入：
 * - Windows 桌面：Windows DPAPI（当前用户范围，见 `desktopApp/Dpapi.kt`）；
 * - 其它平台或不支持时：[protector] 为 null，行为退化为明文（与旧版本一致）。
 *
 * 兼容性：只有带 [MARKER] 前缀的值才尝试解密；旧版本留下的明文值原样返回，
 * 下次写入时自然升级为密文，不需要一次性迁移脚本。
 */
object SecretStore {

    /** 视为凭据、必须加密落盘的键（`SharedPrefs` 里的键名） */
    val SENSITIVE_KEYS: Set<String> = setOf(
        "TGC",              // CAS 票据
        "casCookies",
        "redirect",         // 教务 SESSION
        "ticket",
        "auth",             // 一卡通 / 微信
        "bearer",           // 一网通办
        "TOKEN",            // 社区
        "LibraryToken",     // 图书馆
        "second_class_token",
        "SupabasePsk",      // 社区登录密码
        "loginCode",        // 呱呱洗浴
        "PE",
        "ZhiJian",
        "Password",         // 桌面上已不再写入，这里兜底旧数据
    )

    /**
     * `DataStore` 里的敏感键名（与上面那套不同，值是 protobuf 落盘的）。
     * 包括各类平台密码、JWT / refresh token、Bearer 与 WebVPN 会话 cookie。
     */
    val DATASTORE_SECRET_KEYS: Set<String> = setOf(
        "supabase_jwt",
        "supabase_refresh_token",
        "card_password",
        "webvpn_cookie",
        "wx_auth",
        "xwx_password",
        "jxglstu_password",
        "school_net_password",
        "uni_app_jwt",
        "api_key",
    )

    private const val MARKER = "dpapi1:"

    /** 加密：明文 → 可落盘字符串；返回 null 表示加密失败 */
    var protector: ((String) -> String?)? = null

    /** 解密：落盘字符串 → 明文；返回 null 表示解不开（换用户 / 换机器 / 被篡改） */
    var unprotector: ((String) -> String?)? = null

    fun isSensitive(key: String): Boolean = key in SENSITIVE_KEYS || key in DATASTORE_SECRET_KEYS

    /** 写入前的编码；非敏感键、空值、已编码的值都原样返回 */
    fun encode(key: String, value: String?): String? {
        if (value == null) return null
        if (!isSensitive(key) || value.isEmpty() || value.startsWith(MARKER)) return value
        val enc = protector?.let { runCatching { it(value) }.getOrNull() } ?: return value
        return MARKER + enc
    }

    /** 读取后的解码；纯明文（旧数据）原样返回，密文解不开时返回 null（上层按「未登录」处理） */
    fun decode(key: String, stored: String?): String? {
        if (stored == null) return null
        if (!stored.startsWith(MARKER)) return stored
        val body = stored.removePrefix(MARKER)
        val dec = unprotector ?: return null
        return runCatching { dec(body) }.getOrNull()
    }

    /** 该值是否是本模块写入的密文 */
    fun isEncoded(value: String?): Boolean = value?.startsWith(MARKER) == true
}
