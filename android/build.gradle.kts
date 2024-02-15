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
apply(from = "https://raw.githubusercontent.com/IVIanuu/gradle-scripts/master/kt-compiler-args.gradle")
apply(from = "https://raw.githubusercontent.com/IVIanuu/gradle-scripts/master/kt-source-sets-android.gradle")

dependencies {
  api(Deps.AndroidX.Activity.activity)
  api(Deps.AndroidX.core)
  api(Deps.AndroidX.Lifecycle.runtime)
  api(Deps.Compose.googleFonts)

  api(Deps.AndroidX.dataStore)
  api(Deps.superUser)
  api(Deps.Coroutines.android)

  api(Deps.Coil.coil)
  api(Deps.Coil.coilCompose)

  api(Deps.processPhoenix)

  api(project(":db"))
  api(project(":core"))
  api(project(":ui"))
  testImplementation(project(":android-test"))
}

plugins.apply("com.vanniktech.maven.publish")
