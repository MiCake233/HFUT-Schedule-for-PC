package com.hfut.schedule.logic.util.sys

/**
 * 桌面窗口级回调。
 *
 * 共享层里的「登录成功 / 回首页 / 退出应用」在 Android 上是换 Activity，
 * 桌面端没有等价物，由 main() 里的窗口状态接管，通过这里注册回调。
 * Android 端不注册，走各自的原生实现。
 */
object PlatformHooks {
    /** 登录成功后切到主界面 */
    var onLoginSuccess: (() -> Unit)? = null

    /** 回到首页（欢迎页「同意」按钮等） */
    var onNavigateHome: (() -> Unit)? = null

    /** 退出应用 */
    var onExitApp: (() -> Unit)? = null
}
