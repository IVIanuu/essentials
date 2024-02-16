/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

plugins {
  id("com.android.library")
  id("com.ivianuu.essentials")
  kotlin("android")
}

android {
  defaultConfig {
    consumerProguardFiles("proguard-rules.pro")
  }
}

dependencies {
  api(project(":android"))
  compileOnly(project(":xposed-provided"))
}

plugins.apply("com.vanniktech.maven.publish")
