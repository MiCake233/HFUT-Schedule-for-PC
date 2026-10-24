package com.hfut.schedule.desktop

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hfut.schedule.ui.component.container.CustomCard
import com.hfut.schedule.ui.component.text.DividerTextExpandedWith
import com.xah.common.platform.Platform
import com.xah.common.ui.res.Drawables
import com.xah.common.ui.res.painterResource
import java.awt.Desktop
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date

/** 崩溃日志目录 */
internal fun crashLogDir(): File = File(Platform.dataDirPath(), "logs").apply { mkdirs() }

internal fun crashLogFiles(): List<File> =
    crashLogDir().listFiles { f -> f.isFile && f.name.endsWith(".log") }
        ?.sortedByDescending { it.lastModified() }
        ?: emptyList()

/** 「修复 / 日志」窗口的显示状态 */
object DesktopFixState {
    var visible by mutableStateOf(false)

    fun show() { visible = true }
    fun hide() { visible = false }
}

@Composable
fun DesktopFixScreen(onClose: () -> Unit) {
    var logs by remember { mutableStateOf(crashLogFiles()) }
    var preview by remember { mutableStateOf<String?>(null) }
    val timeFormat = remember { SimpleDateFormat("yyyy-MM-dd HH:mm") }

    LaunchedEffect(Unit) { logs = crashLogFiles() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Text("崩溃日志与诊断", style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
            TextButton(onClick = { openDir(crashLogDir()) }) { Text("打开目录") }
            TextButton(onClick = { logs = crashLogFiles() }) { Text("刷新") }
            TextButton(onClick = onClose) { Text("关闭") }
        }
        DividerTextExpandedWith("日志目录") {
            CustomCard {
                Column(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
                    Text(crashLogDir().absolutePath, style = MaterialTheme.typography.bodySmall)
                    Text(
                        "崩溃时自动写入，文件名含时间戳；日志仅保存在本机。",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        DividerTextExpandedWith("崩溃日志（${logs.size}）") {
            CustomCard {
                if (logs.isEmpty()) {
                    Text(
                        "暂无崩溃记录",
                        modifier = Modifier.padding(12.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    logs.forEachIndexed { index, file ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { preview = if (preview == file.name) null else file.name }
                                .padding(12.dp)
                        ) {
                            Text(file.name, style = MaterialTheme.typography.bodyMedium)
                            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                Text(
                                    timeFormat.format(Date(file.lastModified())),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    "${file.length() / 1024} KB",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        if (preview == file.name) {
                            val text = runCatching { file.readText().take(4000) }.getOrDefault("")
                            Text(
                                text,
                                modifier = Modifier.padding(start = 12.dp, end = 12.dp, bottom = 12.dp),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        if (index != logs.lastIndex) HorizontalDivider()
                    }
                }
            }
        }
        DividerTextExpandedWith("维护") {
            CustomCard {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(painterResource(Drawables.delete), contentDescription = null)
                    Text("清空全部崩溃日志", modifier = Modifier.weight(1f))
                    TextButton(onClick = {
                        crashLogFiles().forEach { runCatching { it.delete() } }
                        logs = crashLogFiles()
                        preview = null
                    }) { Text("清空") }
                }
            }
        }
    }
}

private fun openDir(dir: File) {
    runCatching {
        if (Desktop.isDesktopSupported()) Desktop.getDesktop().open(dir)
    }
}
