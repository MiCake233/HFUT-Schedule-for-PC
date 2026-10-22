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

    val isDev: Boolean
        get() = !Regex("^\\d+\\.\\d+(\\.\\d+)*$").matches(versionName)

    val isNormalEnv: Boolean
        get() = !isDebug && isSignatureValid && !isDev && !isRunningOnAvd && !isRunningOnWsa && !isHarmony

    fun getVersionName(): String = versionName

    fun getVersionCode(): Int = versionCode
}
