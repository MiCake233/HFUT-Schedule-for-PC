package com.hfut.schedule.logic.util.sys

/**
 * 电池温度。
 *
 * Android 读系统电池广播。桌面 JVM（Windows / macOS）没有对等的跨平台 API，
 * 实际实现返回 null，开发者选项里显示「不可用」。
 */
expect object TemperatureUtil {
    fun getBatteryTemperature(): Float?
    fun isOverheating(): Boolean?
}
