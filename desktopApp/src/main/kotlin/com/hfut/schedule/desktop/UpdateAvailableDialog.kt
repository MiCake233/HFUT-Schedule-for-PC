package com.hfut.schedule.desktop

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hfut.schedule.logic.util.sys.Starter
import com.hfut.schedule.logic.util.sys.showToast
import kotlinx.coroutines.launch

/** 启动时发现新版本：弹窗提示下载 */
@Composable
fun UpdateAvailableDialog() {
    val target = DesktopUpdateState.available ?: return
    if (DesktopUpdateState.dismissed) return

    val scope = rememberCoroutineScope()
    var downloading by remember { mutableStateOf(false) }
    var progress by remember { mutableStateOf(0) }

    AlertDialog(
        onDismissRequest = { DesktopUpdateState.dismissed = true },
        title = { Text("发现新版本 ${target.latest}") },
        text = {
            Column(modifier = Modifier.padding(top = 4.dp)) {
                Text("当前版本 ${target.current}，可下载安装包覆盖升级。")
                Text(
                    "升级不会清除本机数据与登录状态。",
                    modifier = Modifier.padding(top = 8.dp)
                )
                if (downloading) {
                    LinearProgressIndicator(
                        progress = { progress / 100f },
                        modifier = Modifier.padding(top = 12.dp)
                    )
                    Text("正在下载 $progress%", modifier = Modifier.padding(top = 6.dp))
                }
            }
        },
        confirmButton = {
            TextButton(enabled = !downloading, onClick = {
                downloading = true
                scope.launch {
                    val outcome = DesktopUpdate.download(target) { done, total ->
                        progress = if (total > 0) (done * 100 / total).toInt() else 0
                    }
                    downloading = false
                    when (outcome) {
                        is DesktopUpdate.Download.Ok -> {
                            if (DesktopUpdate.launchInstaller(outcome.file)) {
                                showToast("已启动安装程序，按提示完成升级")
                                DesktopUpdateState.dismissed = true
                            } else {
                                showToast("无法启动安装程序，请到 ${outcome.file.parent} 手动运行")
                            }
                        }
                        is DesktopUpdate.Download.Error -> showToast(outcome.message)
                    }
                }
            }) { Text(if (downloading) "下载中" else "下载并安装") }
        },
        dismissButton = {
            TextButton(onClick = { Starter.startWebUrlOuter(target.url) }) { Text("打开发布页") }
            TextButton(onClick = { DesktopUpdateState.dismissed = true }) { Text("稍后") }
        }
    )
}
