/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

plugins {
  kotlin("android")
  id("com.android.library")
  id("essentials")
}

android {
  sourceSets["main"].run {
    manifest.srcFile("src/androidMain/AndroidManifest.xml")
    kotlin.srcDirs("src/androidMain/kotlin")
    kotlin.srcDirs("src/commonJvmMain/kotlin")
    kotlin.srcDirs("src/commonMain/kotlin")
  }
}

dependencies {
  api(Deps.circuitFoundation)
  api(Deps.Compose.foundation)
  api(Deps.Compose.material3)
  api(Deps.Compose.materialIconsExtended)
  api(Deps.composeIconsFontAwesome)
  api(Deps.composeReordable)
  api(Deps.materialKolor)
  api(Deps.materialMotionCompose)
  api(project(":core"))
  api(Deps.AndroidX.Activity.compose)
  api(Deps.AndroidX.core)
}

plugins.apply("com.vanniktech.maven.publish")
