package com.hfut.schedule.logic.util.sys

import com.xah.navigation.anim.effect.JumpTransitionEffect
import com.xah.navigation.anim.effect.defaultJumpBackground

/**
 * 页面跳转用的转场效果。
 * 原先定义在 WallpaperUtil.kt（Android 壁纸工具）里，但它其实只用到 SharedNav 的默认背景，
 * 与平台无关，抽到共享源集供两端使用。
 */
// fixme:暂时禁用高级特效，后面流畅了再用
fun JumpTransitionEffectWallpaper() = JumpTransitionEffect(background = defaultJumpBackground, alphaStyle = true)
