/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

plugins {
  kotlin("android")
  id("com.android.library")
  id("essentials")
}

dependencies {
  api(Deps.AndroidX.Activity.activity)
  api(Deps.AndroidX.core)
  api(Deps.AndroidX.Lifecycle.runtime)
  api(Deps.Compose.googleFonts)

  api(Deps.AndroidX.DataStore.dataStore)
  api(Deps.AndroidX.DataStore.preferences)
  api(Deps.superUser)
  api(Deps.Coroutines.android)

  api(Deps.Coil.coil)
  api(Deps.Coil.coilCompose)

  api(Deps.processPhoenix)

  api(project(":core"))
  api(project(":ui"))
  testImplementation(project(":android-test"))
}

plugins.apply("com.vanniktech.maven.publish")
