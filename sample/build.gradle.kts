/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

plugins {
  id("com.android.application")
  id("com.ivianuu.essentials")
  kotlin("android")
}

apply(from = "https://raw.githubusercontent.com/IVIanuu/gradle-scripts/master/android-build-app.gradle")
apply(from = "https://raw.githubusercontent.com/IVIanuu/gradle-scripts/master/android-proguard.gradle")
apply(from = "https://raw.githubusercontent.com/IVIanuu/gradle-scripts/master/java-8-android.gradle")
apply(from = "https://raw.githubusercontent.com/IVIanuu/gradle-scripts/master/kt-compiler-args.gradle")
apply(from = "https://raw.githubusercontent.com/IVIanuu/gradle-scripts/master/kt-source-sets-android.gradle")

android {
  // todo remove once fixed
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
  }
}

dependencies {
  implementation(project(":analytics-android"))
  implementation(project(":about"))
  implementation(project(":accessibility"))
  implementation(project(":ads"))
  implementation(project(":android"))
  implementation(project(":apps"))
  implementation(project(":backup"))
  implementation(project(":billing"))
  implementation(project(":boot"))
  implementation(project(":donation"))
  implementation(project(":foreground"))
  implementation(project(":gestures"))
  implementation(project(":help"))
  implementation(project(":notification-listener"))
  implementation(project(":permission"))
  implementation(project(":premium"))
  implementation(project(":process-restart"))
  implementation(project(":rate"))
  implementation(project(":rubik"))
  implementation(project(":shell"))
  implementation(project(":shortcut-picker"))
  implementation(project(":tile"))
  implementation(project(":torch"))
  implementation(project(":unlock"))
  implementation(project(":work"))
  implementation(project(":xposed"))
  compileOnly(project(":xposed-provided"))
  testImplementation(project(":android-test"))
  testImplementation(project(":test"))
}