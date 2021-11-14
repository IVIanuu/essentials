/*
 * Copyright 2021 Manuel Wrage
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

plugins {
  id("com.android.library")
  id("com.ivianuu.essentials")
  id("org.jetbrains.compose")
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
        api(project(":essentials-logging"))
        api(project(":essentials-resource"))
        api(project(":essentials-time"))
      }
    }

    val commonJvmMain by creating

    val androidMain by getting
    androidMain.dependsOn(commonJvmMain)

    named("androidMain") {
      dependencies {
        api(Deps.Accompanist.flowLayout)
        api(Deps.Accompanist.pager)
        api(Deps.Accompanist.pagerIndicators)
        api(Deps.Accompanist.swipeRefresh)
        api(Deps.AndroidX.Activity.compose)
        api(Deps.AndroidX.core)
        api(Deps.AndroidX.ConstraintLayout.compose)
        api(Deps.Injekt.android)
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
