/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */
plugins {
  kotlin("multiplatform")
  kotlin("plugin.serialization")
  id("com.ivianuu.essentials")
}

kotlin {
  jvm()

  sourceSets {
    commonMain {
      dependencies {
        api(project(":essentials-coroutines"))
        api(project(":essentials-time"))
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

