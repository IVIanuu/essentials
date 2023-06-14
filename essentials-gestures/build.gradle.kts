/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

plugins {
  id("com.android.library")
  id("com.ivianuu.essentials")
  id("com.ivianuu.essentials.compose")
  kotlin("android")
  kotlin("plugin.serialization")
}

apply(from = "https://raw.githubusercontent.com/IVIanuu/gradle-scripts/master/android-build-lib.gradle")
apply(from = "https://raw.githubusercontent.com/IVIanuu/gradle-scripts/master/java-8-android.gradle")
apply(from = "https://raw.githubusercontent.com/IVIanuu/gradle-scripts/master/kt-source-sets-android.gradle")

dependencies {
  api(project(":essentials-accessibility"))
  api(project(":essentials-android-settings"))
  api(project(":essentials-android-util"))
  api(project(":essentials-apps"))
  api(project(":essentials-apps-ui"))
  api(project(":essentials-hide-nav-bar"))
  api(project(":essentials-permission"))
  api(project(":essentials-recent-apps"))
  api(project(":essentials-screen-state"))
  api(project(":essentials-shortcut-picker"))
  api(project(":essentials-system-overlay"))
  api(project(":essentials-torch"))
  api(project(":essentials-unlock"))
  testImplementation(project(":essentials-android-test"))
}

plugins.apply("com.vanniktech.maven.publish")
