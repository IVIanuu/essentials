/*
 * Copyright 2021 Manuel Wrage
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
  implementation(project(":essentials-android"))
  implementation(project(":essentials-android-core"))
  implementation(project(":essentials-android-prefs"))
  implementation(project(":essentials-android-settings"))
  implementation(project(":essentials-about"))
  implementation(project(":essentials-accessibility"))
  implementation(project(":essentials-ads"))
  implementation(project(":essentials-apps"))
  implementation(project(":essentials-apps-shortcuts"))
  implementation(project(":essentials-apps-ui"))
  implementation(project(":essentials-backup"))
  implementation(project(":essentials-billing"))
  implementation(project(":essentials-boot"))
  implementation(project(":essentials-donation"))
  implementation(project(":essentials-foreground"))
  implementation(project(":essentials-gestures"))
  implementation(project(":essentials-hide-nav-bar"))
  implementation(project(":essentials-license"))
  implementation(project(":essentials-notification-listener"))
  implementation(project(":essentials-permission"))
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