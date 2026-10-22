import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.jetbrainsKotlinJvm)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeMultiplatform)
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    implementation(compose.desktop.currentOs)
    implementation(compose.runtime)
    implementation(compose.foundation)
    implementation(compose.ui)
    implementation(compose.components.resources)
    // KMP 版 Material3（CMP 1.12.2 对应 Jetpack M3 1.5.0-alpha22）
    implementation(libs.jb.material3)
    // 桌面端导航/转场/浮窗骨架
    implementation(libs.sharednav.jvm)
    implementation(libs.jb.lifecycle.viewmodel)
    implementation(libs.jb.lifecycle.viewmodel.compose)
    // 模糊
    implementation(libs.haze)
    implementation(libs.haze.materials)
    implementation(libs.kotlinx.coroutines.core)
}

compose.resources {
    publicResClass = true
    packageOfResClass = "hfutprobe.resources"
}

compose.desktop {
    application {
        mainClass = "hfutprobe.MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Msi)
            packageName = "HFUTScheduleProbe"
            packageVersion = "1.0.0"
        }
    }
}

// 无窗口离屏渲染，用于自动化验证 UI 是否真的画得出来
tasks.register<JavaExec>("snapshot") {
    group = "verification"
    description = "离屏渲染探针界面为 PNG"
    dependsOn(tasks.named("classes"))
    mainClass.set("hfutprobe.MainKt")
    classpath = sourceSets["main"].runtimeClasspath
    args = listOf("--snapshot", "E:/dshworkspace/research/hfut-desktop/probe.png")
    systemProperty("java.awt.headless", "false")
}
