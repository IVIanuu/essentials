import org.jetbrains.kotlin.gradle.internal.AndroidExtensionsExtension

plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-kapt")
}

apply(from = "https://raw.githubusercontent.com/IVIanuu/gradle-scripts/master/android-build-app.gradle")
apply(from = "https://raw.githubusercontent.com/IVIanuu/gradle-scripts/master/kt-android-ext.gradle")
apply(from = "https://raw.githubusercontent.com/IVIanuu/gradle-scripts/master/kt-kapt.gradle")

android {
    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
            isShrinkResources = true
        }
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