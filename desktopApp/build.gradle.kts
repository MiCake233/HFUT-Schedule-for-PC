import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.jetbrainsKotlinJvm)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeMultiplatform)
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
    }
}

// 桌面端壳工程：只负责窗口 / 启动流程 / MSI 打包，业务代码全部来自 :app 的 jvm 目标
dependencies {
    implementation(project(":app"))

    implementation(compose.desktop.currentOs)
    implementation(compose.runtime)
    implementation(compose.foundation)
    implementation(compose.ui)
    implementation(compose.components.resources)
    implementation(libs.jb.material3)
    implementation(libs.sharednavJvm)
    implementation(libs.jb.lifecycle.viewmodel)
    implementation(libs.jb.lifecycle.viewmodel.compose)
    // 主题取色（与 Android 端同一套动态配色）
    implementation(libs.material.kolor)
    // 模糊
    implementation(libs.haze)
    implementation(libs.haze.materials)
    // 底栏用到的 Nav2（KMP，桌面有 jvm 变体）
    implementation(libs.navigation2)
    implementation(libs.okhttp)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.swing)
}

/**
 * 版本对齐。
 *
 * Compose Multiplatform 的 `org.jetbrains.*` 坐标是「对齐壳」：本体只有几百字节，
 * 真正实现来自同名的 `androidx.*` 坐标。壳与被依赖的实现版本不一致时，运行时会同时存在
 * `xxx-2.9.6.jar` 与 `xxx-2.10.0.jar` 两份（类不冲突，但可读性与排查成本都变差）。
 * 这里把壳钉到与实现一致的版本，发行包里每个坐标只留一个版本。
 */
configurations.configureEach {
    resolutionStrategy {
        force(
            "org.jetbrains.androidx.lifecycle:lifecycle-runtime-compose:2.10.0",
            "org.jetbrains.androidx.savedstate:savedstate-compose:1.4.0",
            "org.jetbrains.androidx.savedstate:savedstate:1.4.0"
        )
    }
}

compose.desktop {
    application {
        mainClass = "com.hfut.schedule.desktop.MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Msi)
            packageName = "HFUT-Schedule"
            // 1.0.0 有「CAS 登录拿不到 cookie 一直转圈」的缺陷，升到 1.0.1 以便覆盖升级
            packageVersion = "10.0.0"
            description = "HFUT 课程表 桌面版"
            vendor = "HFUT-Schedule Desktop Port"
            copyright = "Apache-2.0"
            // 内置 JRE 的模块清单：Room/SQLite 用到 java.sql，OkHttp 用到 jdk.unsupported，
            // 加密/证书链还需要 jdk.crypto.ec，缺一个都会在启动时抛 NoClassDefFoundError
            modules(
                "java.instrument",
                "java.prefs",
                "java.sql",
                "java.naming",
                "jdk.unsupported",
                "jdk.crypto.ec",
                "jdk.crypto.cryptoki",
                "jdk.management",
                "java.management"
            )
            windows {
                menuGroup = "HFUT-Schedule"
                // 固定升级 UUID，保证 MSI 覆盖安装而不是并存
                upgradeUuid = "8f3c1a2e-6b47-4d19-9c85-2f1a7d4e9b60"
                shortcut = true
                dirChooser = true
                perUserInstall = true
                // 应用图标：icons/app.ico 由 icons/app-512.png 生成（卡通风格自制徽标，
                // 不使用合工大官方校徽原图）。窗口/任务栏图标用同一张 PNG。
                iconFile.set(project.file("icons/app.ico"))
            }
        }
    }
}

// 无窗口离屏渲染，用于自动化验证「界面真的画得出来」
tasks.register<JavaExec>("snapshot") {
    group = "verification"
    description = "离屏渲染桌面端界面为 PNG"
    dependsOn(tasks.named("classes"))
    mainClass.set("com.hfut.schedule.desktop.MainKt")
    classpath = sourceSets["main"].runtimeClasspath
    args = listOf("--snapshot", "E:/dshworkspace/research/hfut-desktop/desktop-home.png")
    systemProperty("java.awt.headless", "false")
}

// 渲染「已登录」状态的主界面
tasks.register<JavaExec>("snapshotHome") {
    group = "verification"
    description = "离屏渲染已登录的桌面主界面为 PNG"
    dependsOn(tasks.named("classes"))
    mainClass.set("com.hfut.schedule.desktop.MainKt")
    classpath = sourceSets["main"].runtimeClasspath
    args = listOf("--snapshot", "E:/dshworkspace/research/hfut-desktop/desktop-main.png", "--logged-in")
    systemProperty("java.awt.headless", "false")
}

// 验证「写入日历」在桌面端导出 .ics
tasks.register<JavaExec>("verifyIcs") {
    group = "verification"
    description = "调用 addToCalendars，检查是否导出 HFUT-Schedule.ics"
    dependsOn(tasks.named("classes"))
    mainClass.set("com.hfut.schedule.desktop.MainKt")
    classpath = sourceSets["main"].runtimeClasspath
    args = listOf("--verify-ics")
    systemProperty("java.awt.headless", "false")
}

// 渲染外观设置页
tasks.register<JavaExec>("snapshotAppearance") {
    group = "verification"
    description = "离屏渲染桌面版外观设置页为 PNG"
    dependsOn(tasks.named("classes"))
    mainClass.set("com.hfut.schedule.desktop.MainKt")
    classpath = sourceSets["main"].runtimeClasspath
    args = listOf("--snapshot", "E:/dshworkspace/research/hfut-desktop/desktop-appearance.png", "--appearance")
    systemProperty("java.awt.headless", "false")
}

// CAS 登录链路诊断
tasks.register<JavaExec>("loginProbe") {
    group = "verification"
    description = "打印 CAS 登录前置条件（dispatcher/prefs/cookie/captcha/login 回调）"
    dependsOn(tasks.named("classes"))
    mainClass.set("com.hfut.schedule.desktop.MainKt")
    classpath = sourceSets["main"].runtimeClasspath
    args = listOf("--login-probe")
    systemProperty("java.awt.headless", "false")
}

// 校验天气图标注册表
tasks.register<JavaExec>("verifyDrawables") {
    group = "verification"
    description = "检查 qweather 图标能否按名字从注册表取到"
    dependsOn(tasks.named("classes"))
    mainClass.set("com.hfut.schedule.desktop.MainKt")
    classpath = sourceSets["main"].runtimeClasspath
    args = listOf("--verify-drawables")
    systemProperty("java.awt.headless", "false")
}

// 校验「桌面端不提供的功能」是否已从 UI 入口列表中过滤掉
tasks.register<JavaExec>("verifyFuncs") {
    group = "verification"
    description = "打印 PlatformFeatures 判定与聚焦页功能列表"
    dependsOn(tasks.named("classes"))
    mainClass.set("com.hfut.schedule.desktop.MainKt")
    classpath = sourceSets["main"].runtimeClasspath
    args = listOf("--verify-funcs")
    systemProperty("java.awt.headless", "false")
}

// 校验当前周计算
tasks.register<JavaExec>("verifyWeek") {
    group = "verification"
    description = "打印学期开始时间、今天与计算出的当前周"
    dependsOn(tasks.named("classes"))
    mainClass.set("com.hfut.schedule.desktop.MainKt")
    classpath = sourceSets["main"].runtimeClasspath
    args = listOf("--verify-week")
    systemProperty("java.awt.headless", "false")
}

// 校验运行时 classpath：不得出现重复类（同名 FQN 出现在两个 jar 里）
// 扫的是程序自己启动后的 java.class.path，因此对安装版同样有效
// （安装版可直接用 HFUT-Schedule.exe --verify-classpath 复跑）。
tasks.register<JavaExec>("verifyClasspath") {
    group = "verification"
    description = "扫描运行时所有 jar，检查是否有重复类"
    dependsOn(tasks.named("classes"))
    mainClass.set("com.hfut.schedule.desktop.MainKt")
    classpath = sourceSets["main"].runtimeClasspath
    args = listOf("--verify-classpath")
    systemProperty("java.awt.headless", "false")
}

// 打出安装包前先跑这道闸门
tasks.matching { it.name == "packageMsi" || it.name == "createDistributable" }
    .configureEach { dependsOn("verifyClasspath") }

// 校验更新检查：版本比较 + 安装包直链 + sha256 解析
tasks.register<JavaExec>("verifyUpdate") {
    group = "verification"
    description = "检查 GitHub 最新正式版解析、安装包直链与 sha256"
    dependsOn(tasks.named("classes"))
    mainClass.set("com.hfut.schedule.desktop.MainKt")
    classpath = sourceSets["main"].runtimeClasspath
    args = listOf("--verify-update")
    systemProperty("java.awt.headless", "false")
    // 开发环境没有 jpackage 注入的版本号，这里假装当前是 9.0.0 以走到「有新版本」分支
    systemProperty("hfut.current-version", "9.0.0")
}

// 打印运行环境标志位（isDebug / 签名位 / isNormalEnv / 版本号）
tasks.register<JavaExec>("verifyEnv") {
    group = "verification"
    description = "检查是否被当作调试版（会影响主界面告警与日志级别）"
    dependsOn(tasks.named("classes"))
    mainClass.set("com.hfut.schedule.desktop.MainKt")
    classpath = sourceSets["main"].runtimeClasspath
    args = listOf("--verify-env")
    systemProperty("java.awt.headless", "false")
}

// 校验备份恢复：正常恢复、zip 越界条目、空备份包
tasks.register<JavaExec>("verifyBackup") {
    group = "verification"
    description = "在临时目录里验证备份恢复是否安全（不碰真实数据）"
    dependsOn(tasks.named("classes"))
    mainClass.set("com.hfut.schedule.desktop.MainKt")
    classpath = sourceSets["main"].runtimeClasspath
    args = listOf("--verify-backup")
    systemProperty("java.awt.headless", "false")
}

// 校验安装包完整性闸门（sha256 不匹配 / 非 MSI 必须被拦下）
tasks.register<JavaExec>("verifyIntegrity") {
    group = "verification"
    description = "检查更新下载的完整性校验是否真的会拦下篡改文件"
    dependsOn(tasks.named("classes"))
    mainClass.set("com.hfut.schedule.desktop.MainKt")
    classpath = sourceSets["main"].runtimeClasspath
    args = listOf("--verify-integrity")
    systemProperty("java.awt.headless", "false")
}

// 校验登录状态持久化与开机自启动
tasks.register<JavaExec>("verifySession") {
    group = "verification"
    description = "检查本地登录凭据、恢复结果与启动项注册状态"
    dependsOn(tasks.named("classes"))
    mainClass.set("com.hfut.schedule.desktop.MainKt")
    classpath = sourceSets["main"].runtimeClasspath
    args = listOf("--verify-session")
    systemProperty("java.awt.headless", "false")
}
