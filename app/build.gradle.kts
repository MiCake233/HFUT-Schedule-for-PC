import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.googleKsp)
    alias(libs.plugins.kotlinParcelize)
}

kotlin {
    androidTarget {
        compilerOptions { jvmTarget.set(JvmTarget.JVM_11) }
    }
    jvm {
        compilerOptions { jvmTarget.set(JvmTarget.JVM_17) }
    }

    // Compose 1.11 起 AVD(动画矢量图)相关 API 需要显式 opt-in（原项目依赖默认 warning 级别）
    compilerOptions {
        optIn.add("androidx.compose.animation.graphics.ExperimentalAnimationGraphicsApi")
        optIn.add("androidx.compose.animation.ExperimentalSharedTransitionApi")
        optIn.add("androidx.compose.material3.ExperimentalMaterial3ExpressiveApi")
        optIn.add("androidx.compose.material3.ExperimentalMaterial3Api")
        optIn.add("androidx.compose.foundation.ExperimentalFoundationApi")
    }

    sourceSets {
        val jvmShared by creating {
            dependsOn(commonMain.get())
            androidMain.get().dependsOn(this)
            jvmMain.get().dependsOn(this)
        }

        // 两端共用的 UI / 业务实现
        jvmShared.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.ui)
            implementation(compose.animation)
            implementation(compose.material)
            implementation(compose.components.resources)
            implementation(libs.jb.material3)
            implementation(libs.jb.lifecycle.viewmodel)
            implementation(libs.jb.lifecycle.viewmodel.compose)
            // Nav2（KMP 坐标，用于带底栏的平切导航）
            implementation(libs.navigation2)

            implementation(libs.kotlinx.coroutines.core)
            // 导航 / 容器共享 / 浮窗骨架（KMP：按目标自动选 android / jvm 变体）
            api(libs.sharednavKmp)
            // 模糊
            implementation(libs.haze)
            implementation(libs.haze.materials)
            // 取色
            implementation(libs.material.kolor)
            // 拖拽
            implementation(libs.reorderable)
            // 数据持久化
            implementation(libs.androidx.datastore.preferences.core)
            implementation(libs.androidx.room.runtime)
            // Room KMP 的 SQLite 抽象（SupportSQLiteDatabase 等）
            implementation(libs.androidx.sqlite)
            // 网络 / 解析
            implementation(libs.jsoup)
            implementation(libs.okhttp)
            implementation(libs.retrofit)
            implementation(libs.gson)
            implementation(libs.zxing)
            // 模块
            api(project(":common-ui"))
            api(project(":common-logic"))
            api(project(":consumption-forecast"))
            api(project(":network-api"))
        }

        androidMain.dependencies {
            implementation(libs.sharednav)
            implementation(libs.androidx.activity.compose)
            implementation(libs.androidx.appcompat)
            implementation(libs.androidx.core.ktx)
            implementation(libs.androidx.lifecycle.runtime.ktx)
            implementation(libs.androidx.lifecycle.livedata.ktx)
            implementation(libs.androidx.lifecycle.viewmodel.ktx)
            implementation(libs.androidx.lifecycle.extensions)
            implementation(libs.androidx.animation.graphics.android)
            implementation(libs.accompanist.systemuicontroller)
            implementation(libs.accompanist.permissions)
            implementation(libs.androidx.core.splashscreen)
            implementation(libs.androidx.palette)
            implementation(libs.androidx.webkit)
            implementation(libs.androidx.work.runtime.ktx)
            implementation(libs.androidx.profileinstaller)
            implementation(libs.androidx.vectordrawable)
            implementation(libs.glide)
            implementation(libs.tesseract)
            implementation(libs.bsdiff)
            implementation(libs.liquid.glass)
            implementation(libs.devicecompat)
            implementation(libs.androidx.camera.view)
            implementation(libs.androidx.camera.core)
            implementation(libs.androidx.camera.camera2)
            implementation(libs.androidx.camera.lifecycle)
            implementation(libs.androidx.glance.appwidget)
            implementation(libs.androidx.glance.material3)
            // 礼花（Android 端用 Konfetti，桌面端自研粒子） 
            implementation(libs.konfetti)
        }

        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.sharednavJvm)
            // 桌面端 SQLite 驱动（Room KMP）
            implementation(libs.androidx.sqlite.bundled)
            implementation(libs.kotlinx.coroutines.swing)
        }
    }
}

compose.resources {
    publicResClass = true
    packageOfResClass = "com.hfut.schedule.resources"
}

android {
    namespace = "com.hfut.schedule"
    compileSdk = Integer.parseInt(libs.versions.maxAndroidVersion.get())

    defaultConfig {
        minSdk = Integer.parseInt(libs.versions.minAndroidVersion.get())
        consumerProguardFiles("consumer-rules.pro")
        // :app 现在是 library，AGP 不再自动生成 APPLICATION_ID，这里补一个
        buildConfigField("String", "APPLICATION_ID", "\"com.hfut.schedule\"")
    }

    buildFeatures {
        compose = true
        aidl = true
        buildConfig = true
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
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].assets.srcDirs("src/androidMain/assets")
    sourceSets["main"].aidl.srcDirs("src/androidMain/aidl")
    sourceSets["main"].java.srcDirs("src/androidMain/kotlin")
    // debug 变体专属资源（原 app/src/debug/res）
    sourceSets["debug"].res.srcDirs("src/androidMain/res-debug")
}

dependencies {
    add("kspAndroid", libs.androidx.room.compiler)
    add("kspJvm", libs.androidx.room.compiler)
}
