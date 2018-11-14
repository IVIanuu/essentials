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
        targetSdkVersion(Build.targetSdk)
        versionCode = Build.versionCode
        versionName = Build.versionName

        javaCompileOptions {
            annotationProcessorOptions {
                argument("dagger.android.experimentalUseStringKeys", "true")
            }
        }
    }

    androidExtensions {
        // isExperimental = true
        configure(delegateClosureOf<AndroidExtensionsExtension> {
            isExperimental = true
        })
    }

    kapt {
        correctErrorTypes = true
    }
}

dependencies {
    implementation(project(":essentials"))
    implementation(project(":essentials-app"))
    implementation(project(":essentials-app-glide"))
    implementation(project(":essentials-shell"))
    implementation(project(":essentials-picker"))
    kapt(project(":essentials-compiler"))

    androidTestImplementation(project("essentials-android-test"))
    testImplementation(project("essentials-test"))
}