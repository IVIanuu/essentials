/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

plugins {
  id("com.ivianuu.essentials")
  id("com.ivianuu.essentials.compose")
  id("kotlinx-atomicfu")
  kotlin("multiplatform")
}

kotlin {
  jvm()

  sourceSets {
    commonMain {
      dependencies {
        api(project(":essentials-coroutines"))
        api(project(":essentials-resource"))
        api(Deps.AtomicFu.runtime)
        api(Deps.Compose.runtime)
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
