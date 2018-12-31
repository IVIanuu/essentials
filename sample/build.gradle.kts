import org.jetbrains.kotlin.gradle.internal.AndroidExtensionsExtension

plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-android-extensions")
    id("kotlin-kapt")
}

android {
    compileSdkVersion(Build.compileSdk)

    defaultConfig {
        applicationId = Build.applicationId
        buildToolsVersion = Build.buildToolsVersion
        minSdkVersion(Build.minSdk)
        targetSdkVersion(Build.targetSdkSample)
        versionCode = Build.versionCode
        versionName = Build.versionName
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
            isShrinkResources = true
        }
    }

    androidExtensions {
        isExperimental = true
    }

    kapt {
        correctErrorTypes = true
    }
}

dependencies {
    implementation(project(":essentials"))
    implementation(project(":essentials-apps"))
    implementation(project(":essentials-apps-glide"))
    implementation(project(":essentials-hidenavbar"))
    implementation(project(":essentials-shell"))
    implementation(project(":essentials-picker"))
    implementation(project(":essentials-securesettings"))
    implementation(project(":essentials-shell"))
    implementation(project(":essentials-work"))
    kapt(project(":essentials-compiler"))

    testImplementation(project(":essentials-test"))
}