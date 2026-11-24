package com.hfut.schedule.network.api.util

import com.xah.common.platform.Platform
import java.io.File
import java.util.Properties

/**
 * 和风天气 Ed25519 签名材料。
 *
 * 私钥不放在源码里，也不打进安装包。按下面的顺序找，三项都有才签名：
 * 1. 系统属性 [PROP_PRIVATE_KEY] / [PROP_PROJECT_ID] / [PROP_KEY_ID]
 * 2. 环境变量 [ENV_PRIVATE_KEY] / [ENV_PROJECT_ID] / [ENV_KEY_ID]
 * 3. 应用数据目录下的 [FILE_NAME]（键名 [FILE_PRIVATE_KEY]、[FILE_PROJECT_ID]、[FILE_KEY_ID]）
 *
 * 数据目录就是桌面端已经在用的那个（`-Dhfut.dataDir` 可改），这个文件不进仓库。
 * 没有完整配置时不签名。
 */
data class QWeatherCredentials(
    val privateKeyPkcs8: String,
    val projectId: String,
    val keyId: String,
) {
    companion object {
        const val PROP_PRIVATE_KEY = "hfut.qweather.privateKey"
        const val PROP_PROJECT_ID = "hfut.qweather.projectId"
        const val PROP_KEY_ID = "hfut.qweather.keyId"

        const val ENV_PRIVATE_KEY = "HFUT_QWEATHER_PRIVATE_KEY"
        const val ENV_PROJECT_ID = "HFUT_QWEATHER_PROJECT_ID"
        const val ENV_KEY_ID = "HFUT_QWEATHER_KEY_ID"

        const val FILE_PRIVATE_KEY = "privateKey"
        const val FILE_PROJECT_ID = "projectId"
        const val FILE_KEY_ID = "keyId"
        const val FILE_NAME = "qweather.properties"

        fun load(): QWeatherCredentials? = load(
            sys = { key -> System.getProperty(key) },
            env = { key -> System.getenv(key) },
            file = defaultFile(),
        )

        fun load(
            sys: (String) -> String?,
            env: (String) -> String?,
            file: File?,
        ): QWeatherCredentials? {
            fromTriple(sys(PROP_PRIVATE_KEY), sys(PROP_PROJECT_ID), sys(PROP_KEY_ID))?.let { return it }
            fromTriple(env(ENV_PRIVATE_KEY), env(ENV_PROJECT_ID), env(ENV_KEY_ID))?.let { return it }
            if (file == null || !file.isFile) return null
            val props = Properties()
            runCatching { file.inputStream().use { props.load(it) } }.getOrElse { return null }
            return fromTriple(
                props.getProperty(FILE_PRIVATE_KEY),
                props.getProperty(FILE_PROJECT_ID),
                props.getProperty(FILE_KEY_ID),
            )
        }

        fun defaultFile(): File? = runCatching {
            File(Platform.dataDirPath(), FILE_NAME)
        }.getOrNull()

        private fun fromTriple(privateKey: String?, projectId: String?, keyId: String?): QWeatherCredentials? {
            val key = privateKey?.trim().orEmpty()
            val project = projectId?.trim().orEmpty()
            val kid = keyId?.trim().orEmpty()
            if (key.isEmpty() || project.isEmpty() || kid.isEmpty()) return null
            return QWeatherCredentials(key, project, kid)
        }
    }
}
