/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

plugins {
  id("com.android.library")
  id("com.ivianuu.essentials")
  kotlin("android")
}

apply(from = "https://raw.githubusercontent.com/IVIanuu/gradle-scripts/master/android-build-lib.gradle")
apply(from = "https://raw.githubusercontent.com/IVIanuu/gradle-scripts/master/java-8-android.gradle")
apply(from = "https://raw.githubusercontent.com/IVIanuu/gradle-scripts/master/kt-source-sets-android.gradle")

dependencies {
  api(project(":essentials-accessibility"))
  api(project(":essentials-android-data"))
  api(project(":essentials-android-util"))
  api(project(":essentials-apps"))
  api(project(":essentials-recent-apps"))
  api(project(":essentials-ui"))
  api(project(":essentials-screen-state"))
  testImplementation(project(":essentials-android-test"))
}

plugins.apply("com.vanniktech.maven.publish")
