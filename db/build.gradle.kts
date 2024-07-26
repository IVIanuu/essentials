/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */
plugins {
  id("com.android.library")
  id("com.ivianuu.essentials")
  kotlin("android")
}

android {
  sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
  sourceSets["main"].kotlin.srcDirs("src/androidMain/kotlin")
  sourceSets["main"].kotlin.srcDirs("src/commonMain/kotlin")
}

dependencies {
  api(project(":core"))
}

plugins.apply("com.vanniktech.maven.publish")

