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
  api(project(":accessibility"))
  api(project(":android"))
  api(project(":apps"))
  api(project(":permission"))
  api(project(":recent-apps"))
  api(project(":screen-state"))
  api(project(":shortcut-picker"))
  api(project(":system-overlay"))
  api(project(":torch"))
  api(project(":unlock"))
  testImplementation(project(":android-test"))
}

plugins.apply("com.vanniktech.maven.publish")
