package com.hfut.schedule.logic.util.sys

import com.hfut.schedule.logic.model.enumeration.BottomBarItems
import com.xah.navigation.controller.NavigationController

/** 桌面端：由窗口层切换到桌面主页 */
actual fun navigateToHome(navController: NavigationController, subStartDestination: BottomBarItems) {
    com.hfut.schedule.logic.util.sys.PlatformHooks.onNavigateHome?.invoke()
}
