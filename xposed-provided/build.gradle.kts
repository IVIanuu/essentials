/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

plugins {
  id("com.android.library")
  kotlin("android")
  id("io.github.ivianuu.essentials")
}

dependencies {
  api(Deps.xposed)
}

plugins.apply("com.vanniktech.maven.publish")
