/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

import com.ivianuu.essentials.gradle.withLicenses

plugins {
  id("com.android.application")
  id("com.ivianuu.essentials")
  id("com.ivianuu.essentials.compose")
  kotlin("android")
  kotlin("plugin.serialization")
}

apply(from = "https://raw.githubusercontent.com/IVIanuu/gradle-scripts/master/android-build-app.gradle")
apply(from = "https://raw.githubusercontent.com/IVIanuu/gradle-scripts/master/android-proguard.gradle")
apply(from = "https://raw.githubusercontent.com/IVIanuu/gradle-scripts/master/java-8-android.gradle")
apply(from = "https://raw.githubusercontent.com/IVIanuu/gradle-scripts/master/kt-compiler-args.gradle")
apply(from = "https://raw.githubusercontent.com/IVIanuu/gradle-scripts/master/kt-source-sets-android.gradle")

essentials {
  withLicenses()
}

android {
  // todo remove once fixed
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
  }
}

dependencies {
  implementation(project(":essentials-analytics-android"))
  implementation(project(":essentials-android"))
  implementation(project(":essentials-android-core"))
  implementation(project(":essentials-android-prefs"))
  implementation(project(":essentials-android-settings"))
  implementation(project(":essentials-android-util"))
  implementation(project(":essentials-about"))
  implementation(project(":essentials-accessibility"))
  implementation(project(":essentials-ads"))
  implementation(project(":essentials-apps"))
  implementation(project(":essentials-apps-ui"))
  implementation(project(":essentials-backup"))
  implementation(project(":essentials-billing"))
  implementation(project(":essentials-boot"))
  implementation(project(":essentials-color-picker"))
  implementation(project(":essentials-donation"))
  implementation(project(":essentials-foreground"))
  implementation(project(":essentials-gestures"))
  implementation(project(":essentials-help"))
  implementation(project(":essentials-hide-nav-bar"))
  implementation(project(":essentials-license"))
  implementation(project(":essentials-notification-listener"))
  implementation(project(":essentials-permission"))
  implementation(project(":essentials-premium"))
  implementation(project(":essentials-process-restart"))
  implementation(project(":essentials-rate"))
  implementation(project(":essentials-rubik"))
  implementation(project(":essentials-shell"))
  implementation(project(":essentials-shortcut-picker"))
  implementation(project(":essentials-tile"))
  implementation(project(":essentials-torch"))
  implementation(project(":essentials-unlock"))
  implementation(project(":essentials-web-ui"))
  implementation(project(":essentials-work"))
  implementation(project(":essentials-xposed"))
  compileOnly(project(":essentials-xposed-provided"))

  testImplementation(project(":essentials-android-test"))
  testImplementation(project(":essentials-test"))
}