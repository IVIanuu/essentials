/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

plugins {
  id("com.android.library")
  id("com.ivianuu.essentials")
  kotlin("android")
}

dependencies {
  api(project(":android"))
  api(project(":donation"))
  api(project(":ui"))
}

plugins.apply("com.vanniktech.maven.publish")
