/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

plugins {
  id("java-gradle-plugin")
  kotlin("jvm")
  id("com.github.gmazzo.buildconfig") version "3.0.2"
  id("com.google.devtools.ksp")
}

gradlePlugin {
  plugins {
    create("essentialsPlugin") {
      id = "com.ivianuu.essentials"
      implementationClass = "com.ivianuu.essentials.gradle.EssentialsPlugin"
    }
    create("composePlugin") {
      id = "com.ivianuu.essentials.compose"
      implementationClass = "com.ivianuu.essentials.gradle.ComposePlugin"
    }
  }
}

buildConfig {
  className("BuildConfig")
  packageName("com.ivianuu.essentials.gradle")
  buildConfigField("String", "VERSION", "\"${property("VERSION_NAME")}\"")
  buildConfigField("String", "COMPOSE_GROUP_ID", "\"androidx.compose.compiler\"")
  buildConfigField("String", "COMPOSE_COMPILER_ARTIFACT_ID", "\"compiler\"")
  buildConfigField("String", "COMPOSE_COMPILER_VERSION", "\"${Deps.Compose.compilerVersion}\"")
}

dependencies {
  implementation(Deps.AutoService.annotations)
  ksp(Deps.AutoService.processor)

  implementation(Deps.Kotlin.gradlePlugin)
  implementation(Deps.Kotlin.gradlePluginApi)

  api(Deps.AtomicFu.gradlePlugin)
  api(Deps.androidGradlePlugin)
  api(Deps.Firebase.crashlyticsGradlePlugin)
  api(Deps.googlePlayServicesGradlePlugin)
  api(Deps.KotlinSerialization.gradlePlugin)
  api(Deps.Injekt.gradlePlugin)
  api(Deps.Ksp.gradlePlugin)
}

plugins.apply("com.vanniktech.maven.publish")
