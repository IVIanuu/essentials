/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

plugins {
  kotlin("multiplatform")
  id("com.ivianuu.essentials")
}

apply(from = "https://raw.githubusercontent.com/IVIanuu/gradle-scripts/master/kt-compiler-args.gradle")

kotlin {
  jvm()

  sourceSets {
    commonMain {
      dependencies {
        api(project(":essentials-core"))
        api(project(":essentials-coroutines"))
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
