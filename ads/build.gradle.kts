/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */
plugins {
  kotlin("android")
  id("com.android.library")
  id("essentials")
}

dependencies {
  api(Deps.Firebase.ads)
  api(project(":android"))
  api(project(":ui"))
}

plugins.apply("com.vanniktech.maven.publish")
