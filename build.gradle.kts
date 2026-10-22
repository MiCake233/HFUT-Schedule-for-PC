plugins {
    // KSP
    alias(libs.plugins.googleKsp) apply false

    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidLibrary) apply false
    // Kotlin版本
    alias(libs.plugins.kotlinAndroid) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.jetbrainsKotlinJvm) apply false
    // KMP CMP
    alias(libs.plugins.composeMultiplatform) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.androidKotlinMultiplatformLibrary) apply false
    alias(libs.plugins.testAndroid) apply false
    alias(libs.plugins.baselineProfile) apply false
//    alias(libs.plugins.testAndroid) apply false
}

// 构建工具链是 JDK 21，但各模块 Kotlin 的 jvmTarget 是 17（Android 端 11）。
// 若不显式钉住，JVM 目标的 Java 编译会按 21 产出字节码，触发
// "Inconsistent JVM Target Compatibility Between Java and Kotlin Tasks"。
// 这里让 KMP 的 JVM 目标 Java 任务与 Kotlin 保持一致（17），Android 任务仍由 AGP 配置。
subprojects {
    tasks.withType<JavaCompile>().configureEach {
        // Android 的 Java 编译由 AGP 按 compileOptions 配置，这里只钉住 JVM 侧
        if (!name.contains("JavaWithJavac")) {
            options.release.set(17)
        }
    }
}
