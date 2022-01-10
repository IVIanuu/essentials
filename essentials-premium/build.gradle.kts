/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */
plugins {
    id("com.android.library")
    id("com.ivianuu.essentials")
    id("org.jetbrains.compose")
    kotlin("android")
}

apply(from = "https://raw.githubusercontent.com/IVIanuu/gradle-scripts/master/android-build-lib.gradle")
apply(from = "https://raw.githubusercontent.com/IVIanuu/gradle-scripts/master/java-8-android.gradle")
apply(from = "https://raw.githubusercontent.com/IVIanuu/gradle-scripts/master/kt-source-sets-android.gradle")

dependencies {
    api(project(":essentials-ads"))
    api(project(":essentials-android-util"))
    api(project(":essentials-billing"))
    api(project(":essentials-broadcast"))
    api(project(":essentials-unlock"))
    api(project(":essentials-ui"))
}

plugins.apply("com.vanniktech.maven.publish")
