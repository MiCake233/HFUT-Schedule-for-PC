package hfutprobe

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.ImageComposeScene
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.compose.runtime.CompositionLocalProvider
import com.xah.container.component.base.SharedContainer
import com.xah.navigation.component.SharedNavHost
import com.xah.navigation.component.rememberNavController
import com.xah.navigation.model.dest.Destination
import com.xah.navigation.util.LocalNavController
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import hfutprobe.resources.Res
import hfutprobe.resources.add
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.skia.EncodedImageFormat
import java.io.File

/**
 * 桌面端可行性探针：
 * 1) SharedNav(jvm) 的导航 / 容器共享 是否能在 Windows 上跑
 * 2) Compose Resources 的 Android 向量 XML 是否能渲染
 * 3) Material3 的 Expressive API 是否存在于 CMP 1.12.2
 * 4) Haze 1.6.2 的模糊是否能在 Windows 上工作
 * 5) CMP 桌面窗口能否正常显示与关闭
 */
object ProbeHome : Destination() {
    override val key = "probe_home"

    @OptIn(ExperimentalMaterial3ExpressiveApi::class)
    @Composable
    override fun Content() {
        val nav = LocalNavController.current
        val hazeState = remember { HazeState() }
        Box(modifier = Modifier.fillMaxSize()) {
            // 背景层（haze 源）
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .hazeSource(hazeState)
                    .background(Color(0xFFAF111D))
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("探针 · 背景层", color = Color.White, fontSize = 22.sp)
                Text("haze source", color = Color.White.copy(alpha = 0.7f))
            }
            // 前景层（haze 效果 + 共享容器）
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .hazeEffect(state = hazeState),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.35f)
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("SharedNav on Desktop", fontSize = 18.sp)
                        Text("key = ${ProbeHome.key}", fontSize = 12.sp)
                    }
                }
                SharedContainer(
                    key = "probe_detail",
                    shape = RoundedCornerShape(16.dp),
                    containerColor = Color(0xFFF7E7E9)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(72.dp)
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text("SharedContainer 卡片", fontSize = 14.sp)
                        Text("点击进入详情页验证转场", fontSize = 11.sp)
                    }
                }
                Button(onClick = { nav.push(ProbeDetail) }) {
                    Icon(
                        painter = painterResource(Res.drawable.add),
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Text("  打开详情页")
                }
                Surface(shape = RoundedCornerShape(12.dp), color = MaterialTheme.colorScheme.secondaryContainer) {
                    Text(
                        "motionScheme = " + MaterialTheme.motionScheme.toString().take(40),
                        fontSize = 10.sp,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }
    }
}

object ProbeDetail : Destination() {
    override val key = "probe_detail"

    @Composable
    override fun Content() {
        val nav = LocalNavController.current
        Box(
            modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surface),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("详情页 (SharedNav push)", fontSize = 20.sp)
                Text("若能看到本页并返回，说明转场体系可用", fontSize = 12.sp)
                Button(onClick = { nav.pop() }, modifier = Modifier.padding(top = 16.dp)) {
                    Text("返回")
                }
            }
        }
    }
}

@Composable
fun ProbeRoot() {
    val owner = remember {
        object : ViewModelStoreOwner {
            override val viewModelStore = ViewModelStore()
        }
    }
    val controller = rememberNavController(ProbeHome)
    CompositionLocalProvider(LocalViewModelStoreOwner provides owner) {
        MaterialTheme {
            SharedNavHost(navController = controller)
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
private fun snapshot(path: String): Boolean {
    return try {
        val scene = ImageComposeScene(width = 900, height = 640, density = Density(1f)) {
            ProbeRoot()
        }
        val image = scene.render()
        val data = image.encodeToData(EncodedImageFormat.PNG) ?: return false
        File(path).outputStream().use { it.write(data.bytes) }
        scene.close()
        println("SNAPSHOT_OK=$path")
        true
    } catch (t: Throwable) {
        println("SNAPSHOT_FAIL=${t::class.simpleName}: ${t.message}")
        t.printStackTrace()
        false
    }
}

fun main(args: Array<String>) {
    println("=== HFUT desktop probe ===")
    println("os=${System.getProperty("os.name")} jvm=${System.getProperty("java.version")}")
    println("sharednav: com.xah.navigation.SharedNavHost loaded = ${runCatching { Class.forName("com.xah.navigation.component.NavHostKt") }.isSuccess}")

    if (args.contains("--snapshot")) {
        val out = args.getOrNull(args.indexOf("--snapshot") + 1) ?: "probe.png"
        val ok = snapshot(out)
        if (!ok) System.exit(2)
        return
    }

    application {
        val state = rememberWindowState(width = 900.dp, height = 640.dp)
        Window(onCloseRequest = ::exitApplication, title = "HFUT 桌面端探针", state = state) {
            ProbeRoot()
        }
        LaunchedEffect(Unit) {
            println("WINDOW_STARTED")
        }
    }
}
