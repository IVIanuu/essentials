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

@file:Suppress("ClassName", "unused")

object Build {
  const val applicationId = "com.ivianuu.essentials.sample"
  const val compileSdk = 31
  const val minSdk = 24
  const val targetSdk = 30
  const val versionCode = 1
  const val versionName = "0.0.1"
}

object Deps {
  object Accompanist {
    private const val version = "0.20.0"
    const val flowLayout = "com.google.accompanist:accompanist-flowlayout:$version"
    const val pager = "com.google.accompanist:accompanist-pager:$version"
    const val pagerIndicators = "com.google.accompanist:accompanist-pager-indicators:$version"
    const val swipeRefresh = "com.google.accompanist:accompanist-swiperefresh:$version"
  }

  const val androidGradlePlugin = "com.android.tools.build:gradle:7.0.1"

  object AndroidX {
    object Activity {
      private const val version = "1.3.1"
      const val activity = "androidx.activity:activity:$version"
      const val compose = "androidx.activity:activity-compose:$version"
    }

    const val core = "androidx.core:core-ktx:1.5.0-alpha05"

    object Compose {
      const val version = "1.0.4"
      const val compiler = "androidx.compose.compiler:compiler:$version"
      const val core = "androidx.compose.ui:ui:$version"
      const val material = "androidx.compose.material:material:$version"
      const val runtime = "androidx.compose.runtime:runtime:$version"
      const val test = "androidx.compose.test:test-core:$version"
    }

    object ConstraintLayout {
      const val compose = "androidx.constraintlayout:constraintlayout-compose:1.0.0-beta02"
    }

    object Lifecycle {
      private const val version = "2.3.1"
      const val runtime = "androidx.lifecycle:lifecycle-runtime-ktx:$version"
    }

    object Test {
      const val core = "androidx.test:core:1.3.0"
      const val junit = "androidx.test.ext:junit:1.1.1"
      const val rules = "androidx.test:rules:1.2.0"
      const val runner = "androidx.test:runner:1.2.0"
    }

    const val work = "androidx.work:work-runtime-ktx:2.7.0"
  }

  const val autoService = "com.google.auto.service:auto-service:1.0-rc7"

  object Coil {
    private const val version = "1.3.2"
    const val coil = "io.coil-kt:coil:$version"
    const val coilCompose = "io.coil-kt:coil-compose:$version"
  }

  const val compileTesting = "com.github.tschuchortdev:kotlin-compile-testing:1.3.6"

  object Coroutines {
    private const val version = "1.5.2"
    const val android = "org.jetbrains.kotlinx:kotlinx-coroutines-android:$version"
    const val core = "org.jetbrains.kotlinx:kotlinx-coroutines-core:$version"
    const val test = "org.jetbrains.kotlinx:kotlinx-coroutines-test:$version"
  }

  const val dexcountGradlePlugin = "com.getkeepsafe.dexcount:dexcount-gradle-plugin:1.0.0"

  const val dokkaGradlePlugin = "org.jetbrains.dokka:dokka-gradle-plugin:1.4.20"

  const val essentialsGradlePlugin =
    "com.ivianuu.essentials:essentials-gradle-plugin:0.0.1-dev1013"

  object Firebase {
    const val ads = "com.google.firebase:firebase-ads:18.3.0"
    const val crashlytics = "com.google.firebase:firebase-crashlytics:17.2.2"
    const val crashlyticsGradlePlugin = "com.google.firebase:firebase-crashlytics-gradle:2.3.0"
  }

  object Injekt {
    private const val version = "0.0.1-dev652"
    const val android = "com.ivianuu.injekt:injekt-android:$version"
    const val androidWork = "com.ivianuu.injekt:injekt-android-work:$version"
    const val core = "com.ivianuu.injekt:injekt-core:$version"
    const val coroutines = "com.ivianuu.injekt:injekt-coroutines:$version"
    const val common = "com.ivianuu.injekt:injekt-common:$version"
    const val compilerPlugin = "com.ivianuu.injekt:injekt-compiler-plugin:$version"
    const val gradlePlugin = "com.ivianuu.injekt:injekt-gradle-plugin:$version"
  }

  const val junit = "junit:junit:4.13"

  const val kotestAssertions = "io.kotest:kotest-assertions-core:4.4.3"

  const val kotlinCompileTesting = "com.github.tschuchortdev:kotlin-compile-testing:1.4.2"

  object Kotlin {
    const val version = "1.5.31"
    const val compilerEmbeddable = "org.jetbrains.kotlin:kotlin-compiler-embeddable:$version"
    const val compiler = "org.jetbrains.kotlin:kotlin-compiler:$version"
    const val gradlePlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:$version"
    const val gradlePluginApi = "org.jetbrains.kotlin:kotlin-gradle-plugin-api:$version"
  }

  object KotlinSerialization {
    const val gradlePlugin = "org.jetbrains.kotlin:kotlin-serialization:${Kotlin.version}"
    const val json = "org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.0"
  }

  const val leakCanary = "com.squareup.leakcanary:leakcanary-android:2.5"

  const val licenseGradlePlugin = "com.jaredsburrows:gradle-license-plugin:0.8.90"

  const val mockk = "io.mockk:mockk:1.11.0"

  const val mavenPublishGradlePlugin = "com.vanniktech:gradle-maven-publish-plugin:0.14.2"

  object Play {
    const val billing = "com.android.billingclient:billing-ktx:3.0.2"
  }

  const val roboelectric = "org.robolectric:robolectric:4.4"

  const val shadowGradlePlugin = "com.github.jengelman.gradle.plugins:shadow:6.1.0"

  const val superUser = "eu.chainfire:libsuperuser:1.0.0.201704021214"

  const val turbine = "app.cash.turbine:turbine:0.2.1"

  const val xposed = "de.robv.android.xposed:api:82"
}
