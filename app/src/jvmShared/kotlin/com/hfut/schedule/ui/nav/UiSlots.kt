package com.hfut.schedule.ui.nav

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.xah.common.ui.res.Drawables
import com.xah.common.ui.res.painterResource

/**
 * 组件级「平台槽位」。
 *
 * 有些**子页面/子组件**只在 Android 端存在（电费、淋浴、慧新登录、校园地图…），
 * 但它们被共享层的聚合页面（查询中心、生活、卡片页等）引用。
 * 这里用运行期注册表让共享页面安全引用：Android 启动时注册真实实现，
 * 桌面端未注册则渲染 [AndroidFeatureHint] 占位。
 */
object UiSlots {
    private val slots = mutableMapOf<String, @Composable (Any?) -> Unit>()

    fun register(key: String, content: @Composable (Any?) -> Unit) {
        slots[key] = content
    }

    fun isRegistered(key: String): Boolean = slots.containsKey(key)

    private val actions = mutableMapOf<String, (Any?) -> Unit>()

    /** 注册「点击动作」类的平台能力（只有 Android 才有的启动流程） */
    fun registerAction(key: String, action: (Any?) -> Unit) {
        actions[key] = action
    }

    /** 取平台动作；桌面端未注册时返回 null，调用方直接跳过即可 */
    fun action(key: String): ((Any?) -> Unit)? = actions[key]

    /** 检查更新（Android 端实现；桌面端没有自更新通道，返回 null） */
    var updateChecker: (suspend (Any?) -> Any?)? = null

    suspend fun checkUpdates(arg: Any?): Any? = updateChecker?.invoke(arg)

    @Composable
    fun Render(key: String, arg: Any? = null, fallback: (@Composable () -> Unit)? = null) {
        val slot = slots[key]
        when {
            slot != null -> slot(arg)
            fallback != null -> fallback()
            else -> AndroidFeatureHint()
        }
    }
}

/** 桌面端未实现功能时的占位提示 */
@Composable
fun AndroidFeatureHint(text: String = "该功能依赖 Android 系统能力，电脑端暂不提供") {
    Box(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surface),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(24.dp)
        ) {
            Icon(
                painter = painterResource(Drawables.desktop_windows),
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}
