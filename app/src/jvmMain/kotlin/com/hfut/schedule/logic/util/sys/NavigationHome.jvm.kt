package com.hfut.schedule.logic.util.sys

import com.hfut.schedule.logic.model.enumeration.BottomBarItems
import com.xah.navigation.controller.NavigationController

/** 桌面端主页尚未接入导航栈，这里先空实现（欢迎页在桌面端不会出现） */
actual fun navigateToHome(navController: NavigationController, subStartDestination: BottomBarItems) = Unit
