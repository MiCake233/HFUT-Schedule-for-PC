package com.hfut.schedule.logic.util.other

/**
 * 版本与运行环境信息（跨平台）。
 *
 * 原 `AppVersion` 直接读 Android 的 Build/PackageManager，桌面端不可用；
 * 这里把「值」与「能力位」抽出到共享层，由各端在启动时注入（Android 在 MyApplication，
 * 桌面在 main() 里）。
 */
object AppInfo {
    /** 平台相关值，启动时注入 */
    var versionName: String = "4.21.2.4"
    var versionCode: Int = 277
    var deviceName: String = "Desktop"
    var isDebug: Boolean = true
    var isSignatureValid: Boolean = false
    var isRunningOnAvd: Boolean = false
    var isRunningOnWsa: Boolean = false
    var isHarmony: Boolean = false

    /** Android: Build.VERSION.SDK_INT；桌面端给一个「现代」值 */
    var sdkInt: Int = 34

    val CAN_HAZE_BLUR_BAR: Boolean get() = sdkInt >= 31
    val CAN_MOTION_BLUR: Boolean get() = sdkInt >= 31
    // 华为、安卓12 单独对渐变模糊适配
    val HAZE_BLUR_FOR_S: Boolean get() = sdkInt == 31 || sdkInt == 32
    val CAN_DYNAMIC_COLOR: Boolean get() = sdkInt >= 31
    val CAN_PREDICTIVE: Boolean get() = sdkInt >= 34
    val CAN_LIVE_UPDATE: Boolean get() = sdkInt >= 36
    val CAN_SHADER: Boolean get() = sdkInt >= 33

    /**
     * 桌面发行版把上游版本号（可能带 `Dev` 后缀）只当作展示文案。
     * 为 true 时，这个后缀不再把正式包判成开发环境。
     */
    var versionLabelIsInformational: Boolean = false

    val isDev: Boolean
        get() = isDevVersionName(versionName, versionLabelIsInformational)

    val isNormalEnv: Boolean
        get() = isNormalEnvironment(
            debug = isDebug,
            signatureValid = isSignatureValid,
            dev = isDev,
            runningOnAvd = isRunningOnAvd,
            runningOnWsa = isRunningOnWsa,
            harmony = isHarmony,
        )

    // 属性与旧函数 API 并存：@JvmName 只改 JVM 签名，Kotlin 调用方不受影响
    @JvmName("versionNameText")
    fun getVersionName(): String = versionName

    @JvmName("versionCodeValue")
    fun getVersionCode(): Int = versionCode
}

private val RELEASE_VERSION = Regex("^\\d+\\.\\d+(\\.\\d+)*$")

/** 上游版本号是否应视为开发包。`informational` 为 true 时后缀只用于展示。 */
fun isDevVersionName(versionName: String, informational: Boolean = false): Boolean =
    !informational && !RELEASE_VERSION.matches(versionName)

/** 与 [AppInfo.isNormalEnv] 相同的判定，便于不改全局状态做测试。 */
fun isNormalEnvironment(
    debug: Boolean,
    signatureValid: Boolean,
    dev: Boolean,
    runningOnAvd: Boolean,
    runningOnWsa: Boolean,
    harmony: Boolean,
): Boolean = !debug && signatureValid && !dev && !runningOnAvd && !runningOnWsa && !harmony
