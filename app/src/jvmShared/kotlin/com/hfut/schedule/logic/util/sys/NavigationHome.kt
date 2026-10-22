package com.hfut.schedule.logic.util.sys

import com.hfut.schedule.logic.model.enumeration.BottomBarItems
import com.xah.navigation.controller.NavigationController

/** 回到首页并清空返回栈 */
fun NavigationController.backToHome(
    subStartDestination: BottomBarItems = BottomBarItems.FOCUS,
) = navigateToHome(this, subStartDestination)

/** 各平台的「回首页」实现：Android 推入 Home 目的地并清栈；桌面端后续接入桌面主页 */
expect fun navigateToHome(navController: NavigationController, subStartDestination: BottomBarItems)
