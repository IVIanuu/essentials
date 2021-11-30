/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

plugins {
  id("com.android.library")
  id("com.ivianuu.essentials")
  id("org.jetbrains.compose")
  kotlin("android")
}

apply(from = "https://raw.githubusercontent.com/IVIanuu/gradle-scripts/master/android-build-lib.gradle")
apply(from = "https://raw.githubusercontent.com/IVIanuu/gradle-scripts/master/java-8-android.gradle")
apply(from = "https://raw.githubusercontent.com/IVIanuu/gradle-scripts/master/kt-compiler-args.gradle")
apply(from = "https://raw.githubusercontent.com/IVIanuu/gradle-scripts/master/kt-source-sets-android.gradle")

dependencies {
  api(project(":essentials-android-util"))
  api(project(":essentials-donation"))
  api(project(":essentials-license"))
  api(project(":essentials-rate"))
  api(project(":essentials-ui"))
  api(project(":essentials-web-ui"))
}

plugins.apply("com.vanniktech.maven.publish")
