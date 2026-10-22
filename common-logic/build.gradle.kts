import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinParcelize)
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

    // Android 与 Windows 桌面都是 JVM 目标：
    // jvmShared 是两者共用的实现源集，可以直接使用 java.*，
    // 只有 android.* / 桌面专有能力才下放到 androidMain / jvmMain。
    sourceSets {
        val jvmShared by creating {
            dependsOn(commonMain.get())
            androidMain.get().dependsOn(this)
            jvmMain.get().dependsOn(this)
        }
        commonMain.dependencies {
            implementation(libs.kotlinx.coroutines.core)
        }
        jvmShared.dependencies {
            implementation(libs.kotlinx.coroutines.core)
        }
    }
}

android {
    namespace = "com.xah.common.logic"
    compileSdk = Integer.parseInt(libs.versions.maxAndroidVersion.get())

    defaultConfig {
        minSdk = Integer.parseInt(libs.versions.minAndroidVersion.get())
        consumerProguardFiles("consumer-rules.pro")
    }

    buildFeatures {
        aidl = true
        buildConfig = true
    }

    // KMP 下 Android 侧源码在 androidMain，清单与 aidl 也一并挪过去
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].aidl.srcDirs("src/androidMain/aidl")

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
}
