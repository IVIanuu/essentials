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
        api(project(":app"))
        api(project(":coroutines"))
        api(project(":data"))
        api(project(":logging"))
      }
    }
    named("jvmTest") {
      dependencies {
        implementation(project(":test"))
      }
    }
  }
}

plugins.apply("com.vanniktech.maven.publish")
