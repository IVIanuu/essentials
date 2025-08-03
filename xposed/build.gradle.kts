/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

plugins {
  id("com.android.library")
  kotlin("android")
  id("io.github.ivianuu.essentials")
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
