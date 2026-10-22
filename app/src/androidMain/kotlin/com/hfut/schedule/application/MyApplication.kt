package com.hfut.schedule.application

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.hfut.schedule.BuildConfig
import com.xah.common.logic.model.Campus
import com.hfut.schedule.logic.model.enumeration.AppStatus
import com.hfut.schedule.logic.model.Location
import com.hfut.schedule.logic.util.dev.CrashHandler
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.logic.util.sys.CourseLiveUpdateScheduler
import com.hfut.schedule.logic.util.sys.datetime.DateTimeManager
import com.hfut.schedule.network.api.model.Constant
import com.hfut.schedule.ui.nav.deepLinks
import com.hfut.schedule.ui.util.state.GlobalEventHolder
import com.xah.navigation.registry.DeepLinkRegistry
import com.xah.common.logic.util.LogUtil
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Collections

class MyApplication : Application() {

    // 这里必须严格限制，不许放入太耗时的方法，不是不得不在Activity之前初始化的函数别放这里，放Activity里或者Compose里都可以
    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate() {
        super.onCreate()
        // 暴露全局Context
        context = applicationContext
        // 把平台信息注入共享层（桌面端在 main() 里注入）
        com.hfut.schedule.logic.util.other.AppInfo.apply {
            versionName = com.hfut.schedule.logic.util.other.AppVersion.getVersionName()
            versionCode = com.hfut.schedule.logic.util.other.AppVersion.getVersionCode()
            deviceName = com.hfut.schedule.logic.util.other.AppVersion.deviceName
            isDebug = com.hfut.schedule.logic.util.other.AppVersion.isDebug
            isSignatureValid = com.hfut.schedule.logic.util.other.AppVersion.isSignatureValid
            isRunningOnAvd = com.hfut.schedule.logic.util.other.AppVersion.isRunningOnAvd
            isRunningOnWsa = com.hfut.schedule.logic.util.other.AppVersion.isRunningOnWsa
            isHarmony = com.hfut.schedule.logic.util.other.AppVersion.isHarmony
            sdkInt = com.hfut.schedule.logic.util.other.AppVersion.sdkInt
        }
        com.xah.common.platform.PlatformAndroid.init(applicationContext)
        // 初始化崩溃处理
        CrashHandler.init()
        // 初始化日志工具
        LogUtil.init(APP_NAME)
        // 控制SharedNav库的日志
        com.sharednav.common.util.LogUtil.init("SharedNav(${APP_NAME})", BuildConfig.DEBUG)
        // 注册DeepLink
        DeepLinkRegistry.init(deepLinks)
        GlobalScope.launch {
            // 初始化周数（为课程表服务）
            DateTimeManager.initCurrentWeekValue()
            // Added by @Junpgle 为实时通知服务，如需修改代码请找Ta
            if (DataStoreManager.enableLiveCourseReminder.first()) {
                CourseLiveUpdateScheduler.scheduleAll()
                CourseLiveUpdateScheduler.showCurrentWindowCourses()
            }
        }
        // 监听应用前后台变化
        ProcessLifecycleOwner.get().lifecycle.addObserver(
            object : DefaultLifecycleObserver {
                override fun onStart(owner: LifecycleOwner) {
                    GlobalEventHolder.appStatusChangeCallback.emit(AppStatus.FOREGROUND)
                }

                override fun onStop(owner: LifecycleOwner) {
                    GlobalEventHolder.appStatusChangeCallback.emit(AppStatus.BACKGROUND)
                }
            }
        )
        // 监听Activity栈
        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityCreated(a: Activity, b: Bundle?) {
                activities.add(a)
            }

            override fun onActivityDestroyed(a: Activity) {
                activities.remove(a)
            }

            override fun onActivityPaused(activity: Activity) {}

            override fun onActivityResumed(activity: Activity) {
                // Added by @Junpgle 为实时通知服务，如需修改代码请找Ta
                GlobalScope.launch {
                    if (DataStoreManager.enableLiveCourseReminder.first()) {
                        CourseLiveUpdateScheduler.showCurrentWindowCourses()
                    }
                }
            }

            override fun onActivitySaveInstanceState(
                activity: Activity,
                outState: Bundle
            ) {}

            override fun onActivityStarted(activity: Activity) {}

            override fun onActivityStopped(activity: Activity) {}
        })
    }

    companion object {
        // 全局动画速度ANIMATION_SPEED=400ms已经迁移到AnimationManager
        // 全局上下文
        lateinit var context: Context
        // 常量已抽到跨平台的 AppConst（桌面端同样可用），这里转为别名保持旧代码兼容
        const val CALENDAR_SQUARE_HEIGHT = AppConst.CALENDAR_SQUARE_HEIGHT
        const val CALENDAR_SQUARE_TEXT_PADDING = AppConst.CALENDAR_SQUARE_TEXT_PADDING
        const val CALENDAR_SQUARE_ALPHA = AppConst.CALENDAR_SQUARE_ALPHA
        const val CALENDAR_SQUARE_BLUR = AppConst.CALENDAR_SQUARE_BLUR
        const val SWIPE = AppConst.SWIPE
        const val BLUR_RADIUS = AppConst.BLUR_RADIUS
        const val DEFAULT_MAX_FREE_FLOW = AppConst.DEFAULT_MAX_FREE_FLOW
        const val MAX_WEEK = AppConst.MAX_WEEK
        val campusLocations get() = AppConst.campusLocations
        val contributors get() = AppConst.contributors
        const val APP_NAME = AppConst.APP_NAME
        const val EMAIL = AppConst.EMAIL
        // Activity栈
        private val activities = Collections.synchronizedList(mutableListOf<Activity>())
        // 安全地退出App
        fun exitAppSafely() {
            activities.toList().forEach { activity ->
                activity.finish()
            }
        }
        // 获取当前Activity
        fun getCurrentActivity() = activities.lastOrNull()
    }
}

