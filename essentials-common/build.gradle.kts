/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */
plugins {
  kotlin("multiplatform")
  id("com.ivianuu.essentials")
}

kotlin {
  jvm()

  sourceSets {
    commonMain {
      dependencies {
        api(project(":essentials-app"))
        api(project(":essentials-coroutines"))
        api(project(":essentials-data"))
        api(project(":essentials-logging"))
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
