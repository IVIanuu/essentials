/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

plugins {
  id("java-gradle-plugin")
  kotlin("jvm")
  kotlin("kapt")
  id("com.github.gmazzo.buildconfig") version "3.0.2"
}

apply(from = "https://raw.githubusercontent.com/IVIanuu/gradle-scripts/master/java-8.gradle")

gradlePlugin {
  plugins {
    create("essentialsPlugin") {
      id = "com.ivianuu.essentials"
      implementationClass = "com.ivianuu.essentials.gradle.EssentialsPlugin"
    }
  }
}

buildConfig {
  className("BuildConfig")
  packageName("com.ivianuu.essentials.gradle")
  buildConfigField("String", "VERSION", "\"${property("VERSION_NAME")}\"")
}

dependencies {
  implementation(Deps.autoService)
  kapt(Deps.autoService)

  implementation(Deps.Kotlin.gradlePlugin)
  implementation(Deps.Kotlin.gradlePluginApi)

  api(Deps.AtomicFu.gradlePlugin)
  api(Deps.Compose.gradlePlugin)
  api(Deps.androidGradlePlugin)
  api(Deps.KotlinSerialization.gradlePlugin)
  api(Deps.licenseGradlePlugin)
}

plugins.apply("com.vanniktech.maven.publish")
