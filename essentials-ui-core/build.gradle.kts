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
  sourceSets["main"].run {
    manifest.srcFile("src/androidMain/AndroidManifest.xml")
    java.srcDirs("src/androidMain/java")
  }
}

kotlin {
  jvm()
  android {
    publishLibraryVariants("release")
  }

  sourceSets {
    commonMain {
      dependencies {
        api(Deps.Compose.foundation)
        api(Deps.Compose.material)
        api(Deps.Compose.runtime)
        api(project(":essentials-app"))
        api(project(":essentials-compose"))
        api(project(":essentials-logging"))
        api(project(":essentials-serialization"))
      }
    }

    val commonJvmMain by creating

    val androidMain by getting
    androidMain.dependsOn(commonJvmMain)

    named("androidMain") {
      dependencies {
        api(Deps.Accompanist.flowLayout)
        api(Deps.Accompanist.pagerIndicators)
        api(Deps.AndroidX.Activity.compose)
        api(Deps.AndroidX.core)
      }
    }

    val jvmMain by getting
    jvmMain.dependsOn(commonJvmMain)

    named("jvmTest") {
      dependencies {
        implementation(project(":essentials-test"))
      }
    }
  }
}

plugins.apply("com.vanniktech.maven.publish")
