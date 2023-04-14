/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

@file:Suppress("ClassName", "unused")

object Build {
  const val applicationId = "com.ivianuu.essentials.sample"
  const val compileSdk = 33
  const val minSdk = 27
  const val targetSdk = 31
  const val versionCode = 1
  const val versionName = "0.0.1"
}

object Deps {
  object Accompanist {
    private const val version = "0.30.1"
    const val flowLayout = "com.google.accompanist:accompanist-flowlayout:$version"
    const val pager = "com.google.accompanist:accompanist-pager:$version"
    const val pagerIndicators = "com.google.accompanist:accompanist-pager-indicators:$version"
  }

  const val androidGradlePlugin = "com.android.tools.build:gradle:7.4.0"

  object AndroidX {
    object Activity {
      private const val version = "1.7.0"
      const val activity = "androidx.activity:activity:$version"
      const val compose = "androidx.activity:activity-compose:$version"
    }

    const val core = "androidx.core:core-ktx:1.9.0"

    const val dataStore = "androidx.datastore:datastore:1.0.0"

    object Lifecycle {
      private const val version = "2.6.1"
      const val runtime = "androidx.lifecycle:lifecycle-runtime-ktx:$version"
    }

    object Test {
      const val core = "androidx.test:core-ktx:1.4.0"
      const val junit = "androidx.test.ext:junit:1.1.1"
      const val rules = "androidx.test:rules:1.2.0"
      const val runner = "androidx.test:runner:1.2.0"
    }

    const val work = "androidx.work:work-runtime-ktx:2.8.1"
  }

  object AtomicFu {
    private const val version = "0.20.2"
    const val gradlePlugin = "org.jetbrains.kotlinx:atomicfu-gradle-plugin:$version"
    const val runtime = "org.jetbrains.kotlinx:atomicfu:$version"
  }

  const val autoService = "com.google.auto.service:auto-service:1.0-rc7"

  object Coil {
    private const val version = "2.2.1"
    const val coil = "io.coil-kt:coil:$version"
    const val coilCompose = "io.coil-kt:coil-compose:$version"
  }

  object Compose {
    const val version = "1.4.0"
    const val compilerVersion = "1.4.5"
    const val compiler = "org.jetbrains.compose.compiler:compiler:$compilerVersion"
    const val foundation = "org.jetbrains.compose.foundation:foundation:$version"
    const val material = "org.jetbrains.compose.material:material:$version"
    const val runtime = "org.jetbrains.compose.runtime:runtime:$version"
  }

  object Coroutines {
    private const val version = "1.6.4"
    const val android = "org.jetbrains.kotlinx:kotlinx-coroutines-android:$version"
    const val core = "org.jetbrains.kotlinx:kotlinx-coroutines-core:$version"
    const val test = "org.jetbrains.kotlinx:kotlinx-coroutines-test:$version"
  }

  const val dokkaGradlePlugin = "org.jetbrains.dokka:dokka-gradle-plugin:1.8.10"

  const val essentialsGradlePlugin =
    "com.ivianuu.essentials:essentials-gradle-plugin:0.0.1-dev1164"

  object Firebase {
    const val ads = "com.google.firebase:firebase-ads:20.6.0"
    const val analytics = "com.google.firebase:firebase-analytics-ktx:21.0.0"
    const val crashlytics = "com.google.firebase:firebase-crashlytics-ktx:18.2.10"
    const val crashlyticsGradlePlugin = "com.google.firebase:firebase-crashlytics-gradle:2.8.1"
  }

  const val googlePlayServicesGradlePlugin = "com.google.gms:google-services:4.3.10"

  object Injekt {
    private const val version = "0.0.1-dev705"
    const val android = "com.ivianuu.injekt:injekt-android:$version"
    const val androidWork = "com.ivianuu.injekt:injekt-android-work:$version"
    const val common = "com.ivianuu.injekt:injekt-common:$version"
    const val core = "com.ivianuu.injekt:injekt-core:$version"
    const val coroutines = "com.ivianuu.injekt:injekt-coroutines:$version"
    const val compilerPlugin = "com.ivianuu.injekt:injekt-compiler-plugin:$version"
    const val gradlePlugin = "com.ivianuu.injekt:injekt-gradle-plugin:$version"
  }

  const val junit = "junit:junit:4.13"

  const val kotestAssertions = "io.kotest:kotest-assertions-core:5.4.2"

  const val kotlinCompileTesting = "dev.zacsweers.kctfork:core:0.2.1"

  object Kotlin {
    const val version = "1.8.20"
    const val compilerEmbeddable = "org.jetbrains.kotlin:kotlin-compiler-embeddable:$version"
    const val compiler = "org.jetbrains.kotlin:kotlin-compiler:$version"
    const val gradlePlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:$version"
    const val gradlePluginApi = "org.jetbrains.kotlin:kotlin-gradle-plugin-api:$version"
  }

  object KotlinSerialization {
    const val gradlePlugin = "org.jetbrains.kotlin:kotlin-serialization:${Kotlin.version}"
    const val json = "org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.0"
  }

  const val leakCanary = "com.squareup.leakcanary:leakcanary-android:2.5"

  const val licenseGradlePlugin = "com.jaredsburrows:gradle-license-plugin:0.9.0"

  const val mockk = "io.mockk:mockk:1.12.8"

  const val mavenPublishGradlePlugin = "com.vanniktech:gradle-maven-publish-plugin:0.24.0"

  const val playBillingKtx = "com.android.billingclient:billing-ktx:5.1.0"

  const val roboelectric = "org.robolectric:robolectric:4.4"

  const val shadowGradlePlugin = "gradle.plugin.com.github.johnrengelman:shadow:7.1.0"

  const val superUser = "eu.chainfire:libsuperuser:1.1.1"

  const val turbine = "app.cash.turbine:turbine:0.10.0"

  const val xposed = "de.robv.android.xposed:api:82"
}
