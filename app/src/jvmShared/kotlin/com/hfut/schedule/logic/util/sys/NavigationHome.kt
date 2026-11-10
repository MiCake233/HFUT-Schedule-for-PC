package com.hfut.schedule.logic.util.sys

import com.hfut.schedule.logic.model.enumeration.BottomBarItems
import com.hfut.schedule.ui.nav.destination.HomeDestination
import com.xah.navigation.anim.effect.ScaleTransitionEffect
import com.xah.navigation.controller.NavigationController
import com.xah.navigation.model.action.LaunchMode

/** 回到首页并清空返回栈。simplyEffect 是上游更新完成页用的缩放转场。 */
fun NavigationController.backToHome(
    subStartDestination: BottomBarItems = BottomBarItems.FOCUS,
    simplyEffect: Boolean = false,
) {
    if (simplyEffect) {
        push(
            HomeDestination(),
            launchMode = LaunchMode.Clear(),
            effect = ScaleTransitionEffect(false, true),
        )
    } else {
        navigateToHome(this, subStartDestination)
    }
}

/** 各平台的「回首页」实现：Android 推入 Home 目的地并清栈；桌面端后续接入桌面主页 */
expect fun navigateToHome(navController: NavigationController, subStartDestination: BottomBarItems)
