import org.jetbrains.kotlin.gradle.tasks.*

/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

plugins {
  id("java-gradle-plugin")
  kotlin("jvm")
  id("com.github.gmazzo.buildconfig") version "3.0.2"
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

tasks.withType<KotlinCompile> {
  kotlinOptions.freeCompilerArgs += listOf("-Xskip-prerelease-check")
}

buildConfig {
  className("BuildConfig")
  packageName("com.ivianuu.essentials.gradle")
  buildConfigField("String", "VERSION", "\"${property("VERSION_NAME")}\"")
}

dependencies {
  implementation(Deps.Kotlin.gradlePlugin)
  implementation(Deps.Kotlin.gradlePluginApi)

  api(Deps.Compose.gradlePlugin)
  api(Deps.AtomicFu.gradlePlugin)
  api(Deps.androidGradlePlugin)
  api(Deps.Firebase.crashlyticsGradlePlugin)
  api(Deps.googlePlayServicesGradlePlugin)
  api(Deps.KotlinSerialization.gradlePlugin)
  api(Deps.Injekt.gradlePlugin)
  api(Deps.Ksp.gradlePlugin)
}

plugins.apply("com.vanniktech.maven.publish")
