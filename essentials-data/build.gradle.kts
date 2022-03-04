/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */
plugins {
  kotlin("multiplatform")
  kotlin("plugin.serialization")
  id("com.ivianuu.essentials")
  id("org.jetbrains.compose")
}

kotlin {
  jvm()

  sourceSets {
    commonMain {
      dependencies {
        api(project(":essentials-db"))
        api(project(":essentials-resource"))
        api(project(":essentials-serialization"))
        api(project(":essentials-state"))
        api(compose.runtime)
        api(Deps.Coroutines.core)
        api(Deps.Injekt.core)
        api(Deps.Injekt.common)
      }
    }
    named("jvmTest") {
      dependencies {
        implementation(project(":essentials-test"))
      }
    }
  }
}

plugins.apply("com.vanniktech.maven.publish")

