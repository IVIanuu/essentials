/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

plugins {
  id("com.android.library")
  id("com.ivianuu.essentials")
  id("com.ivianuu.essentials.compose")
  kotlin("android")
  kotlin("plugin.serialization")
}

apply(from = "https://raw.githubusercontent.com/IVIanuu/gradle-scripts/master/android-build-lib.gradle")
apply(from = "https://raw.githubusercontent.com/IVIanuu/gradle-scripts/master/java-8-android.gradle")
apply(from = "https://raw.githubusercontent.com/IVIanuu/gradle-scripts/master/kt-compiler-args.gradle")
apply(from = "https://raw.githubusercontent.com/IVIanuu/gradle-scripts/master/kt-source-sets-android.gradle")

dependencies {
  api(Deps.AndroidX.Activity.activity)
  api(Deps.AndroidX.Activity.compose)
  api(Deps.AndroidX.core)
  api(Deps.AndroidX.Lifecycle.runtime)

  api(Deps.Coroutines.android)

  api(Deps.Injekt.android)

  api(project(":essentials-common"))
  api(project(":essentials-android-core"))
  api(project(":essentials-android-data"))
  api(project(":essentials-logging-android"))
  api(project(":essentials-android-util"))
  api(project(":essentials-ui"))
}

plugins.apply("com.vanniktech.maven.publish")
