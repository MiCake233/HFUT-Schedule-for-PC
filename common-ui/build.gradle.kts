import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeMultiplatform)
}

kotlin {
    androidTarget {
        compilerOptions { jvmTarget.set(JvmTarget.JVM_11) }
    }
    jvm {
        compilerOptions { jvmTarget.set(JvmTarget.JVM_17) }
    }

    sourceSets {
        val jvmShared by creating {
            dependsOn(commonMain.get())
            androidMain.get().dependsOn(this)
            jvmMain.get().dependsOn(this)
        }

        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.ui)
            // Compose Resources：全应用共用的图标/文案资源都放在本模块
            api(compose.components.resources)
            implementation(libs.jb.material3)
            implementation(libs.kotlinx.coroutines.core)
            api(project(":common-logic"))
        }

        jvmShared.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.ui)
            implementation(libs.jb.material3)
            implementation(libs.kotlinx.coroutines.core)
        }

        androidMain.dependencies {
            // 透明状态栏（仅 Android）
            implementation(libs.accompanist.systemuicontroller)
            implementation(libs.androidx.activity.compose)
            implementation(libs.androidx.vectordrawable)
        }
    }
}

compose.resources {
    publicResClass = true
    packageOfResClass = "com.xah.common.ui.resources"
}

android {
    namespace = "com.xah.common.ui"
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
