import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.composeCompiler)
}

android {
    namespace = "com.hfut.schedule.androidapp"
    compileSdk = Integer.parseInt(libs.versions.maxAndroidVersion.get())

    defaultConfig {
        applicationId = "com.hfut.schedule"
        minSdk = Integer.parseInt(libs.versions.minAndroidVersion.get())
        targetSdk = Integer.parseInt(libs.versions.maxAndroidVersion.get())
        versionCode = Integer.parseInt(project.findProperty("VERSION_CODE").toString())
        versionName = project.findProperty("VERSION_NAME").toString()
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    buildTypes {
        debug {
            val useDebugPackageSuffix =
                (project.findProperty("USE_DEBUG_PACKAGE_SUFFIX") as? String)?.toBoolean() ?: false
            if (useDebugPackageSuffix) {
                applicationIdSuffix = ".debug"
            }
        }
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

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    dependenciesInfo {
        // F-Droid
        includeInApk = false
        includeInBundle = false
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_11)
    }
}

dependencies {
    implementation(project(":app"))
}
