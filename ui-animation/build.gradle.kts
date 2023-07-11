/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

plugins {
  id("com.ivianuu.essentials")
  kotlin("multiplatform")
}

kotlin {
  jvm()

  sourceSets {
    commonMain {
      dependencies {
        api(project(":ui-core"))
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
