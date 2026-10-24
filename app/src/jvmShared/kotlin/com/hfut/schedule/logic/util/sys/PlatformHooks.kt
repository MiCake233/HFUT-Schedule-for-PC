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

    /**
     * 桌面端可以为某个平台页面 key 提供自己的实现。
     * Android 专属页面（如设置页）在桌面上往往需要完全不同的 UI，
     * 注册后 RenderPlatformScreen 会优先渲染它，返回 null 则继续走占位逻辑。
     */
    var platformScreenFactory: ((String) -> (@androidx.compose.runtime.Composable () -> Unit)?)? = null

    /** 打开「修复 / 日志」页（桌面端为独立窗口） */
    var onShowFix: (() -> Unit)? = null
}
