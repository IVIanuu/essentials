/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */
plugins {
  id("com.android.library")
  id("com.ivianuu.essentials")
  kotlin("multiplatform")
}

apply(from = "https://raw.githubusercontent.com/IVIanuu/gradle-scripts/master/android-build-lib.gradle")
apply(from = "https://raw.githubusercontent.com/IVIanuu/gradle-scripts/master/java-8-android.gradle")

android {
  sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
}

kotlin {
  jvm()
  android {
    publishLibraryVariants("release")
  }

  sourceSets {
    commonMain {
      dependencies {
        api(project(":coroutines"))
        api(project(":core"))
        api(project(":serialization"))
      }
    }
    named("androidUnitTest") {
      dependencies {
        implementation(project(":android-test"))
      }
    }
  }
}

plugins.apply("com.vanniktech.maven.publish")
