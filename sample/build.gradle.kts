/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

plugins {
  kotlin("android")
  id("com.android.application")
  id("io.github.ivianuu.essentials")
}

android {
  defaultConfig {
    versionName = Build.versionName
    versionCode = Build.versionCode
  }
}

dependencies {
  implementation(project(":analytics-android"))
  implementation(project(":accessibility"))
  implementation(project(":ads"))
  implementation(project(":android"))
  implementation(project(":billing"))
  implementation(project(":donation"))
  implementation(project(":foreground"))
  implementation(project(":gestures"))
  implementation(project(":notification-listener"))
  implementation(project(":premium"))
  implementation(project(":work"))
  implementation(project(":xposed"))
  compileOnly(project(":xposed-provided"))
  testImplementation(project(":android-test"))
  testImplementation(project(":test"))
}
