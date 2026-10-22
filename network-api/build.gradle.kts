import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinMultiplatform)
}

kotlin {
    androidTarget {
        compilerOptions { jvmTarget.set(JvmTarget.JVM_11) }
    }
    // 桌面 JVM 目标与 JDK toolchain(17) 对齐，避免 Java/Kotlin JVM target 校验失败；
    // Android 端仍保持 Java 11 字节码。
    jvm {
        compilerOptions { jvmTarget.set(JvmTarget.JVM_17) }
    }

    sourceSets {
        val jvmShared by creating {
            dependsOn(commonMain.get())
            androidMain.get().dependsOn(this)
            jvmMain.get().dependsOn(this)
        }
        jvmShared.dependencies {
            // Jsoup
            implementation(libs.jsoup)
            // OkHttp
            implementation(libs.okhttp)
            // Retrofit
            implementation(libs.retrofit)
            implementation(libs.scalars)
            implementation(libs.gson)
            // 用于和风天气密钥生成的JWT
            implementation(libs.eddsa)
            api(project(":common-logic"))
            api(project(":network-core"))
        }
    }
}

android {
    namespace = "com.hfut.schedule.network.api"
    compileSdk = Integer.parseInt(libs.versions.maxAndroidVersion.get())

    defaultConfig {
        minSdk = Integer.parseInt(libs.versions.minAndroidVersion.get())
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
}
