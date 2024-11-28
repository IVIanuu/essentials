/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

@file:Suppress("ClassName", "unused")

object Build {
  const val applicationId = "com.ivianuu.essentials.sample"
  const val compileSdk = 34
  const val minSdk = 28
  const val targetSdk = 31
  const val versionCode = 1
  const val versionName = "0.0.1"
}

object Deps {
  object Accompanist {
    private const val version = "0.36.0"
    const val flowLayout = "com.google.accompanist:accompanist-flowlayout:$version"
    const val pagerIndicators = "com.google.accompanist:accompanist-pager-indicators:$version"
  }

  const val androidGradlePlugin = "com.android.tools.build:gradle:8.2.2"

  object AndroidX {
    object Activity {
      private const val version = "1.9.3"
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

  object Arrow {
    private const val version = "1.2.0"
    const val atomic = "io.arrow-kt:arrow-atomic:$version"
    const val core = "io.arrow-kt:arrow-core:$version"
    const val fxCoroutines = "io.arrow-kt:arrow-fx-coroutines:$version"
    const val resilience = "io.arrow-kt:arrow-resilience:$version"
  }

  object AtomicFu {
    private const val version = "0.25.0"
    const val gradlePlugin = "org.jetbrains.kotlinx:atomicfu-gradle-plugin:$version"
    const val runtime = "org.jetbrains.kotlinx:atomicfu:$version"
  }

  object AutoService {
    private const val version = "1.1.0"
    const val annotations = "com.google.auto.service:auto-service-annotations:$version"
    const val processor = "dev.zacsweers.autoservice:auto-service-ksp:$version"
  }

  const val circuitFoundation = "com.slack.circuit:circuit-foundation:0.19.1"

  object Coil {
    private const val version = "2.2.1"
    const val coil = "io.coil-kt:coil:$version"
    const val coilCompose = "io.coil-kt:coil-compose:$version"
  }

  object Compose {
    const val version = "1.7.0"
    const val gradlePlugin = "org.jetbrains.kotlin:compose-compiler-gradle-plugin:${Kotlin.version}"
    const val foundation = "org.jetbrains.compose.foundation:foundation:$version"
    const val googleFonts = "androidx.compose.ui:ui-text-google-fonts:$version"
    const val material = "org.jetbrains.compose.material:material:$version"
    const val materialIconsExtended = "org.jetbrains.compose.material:material-icons-extended:$version"
    const val runtime = "org.jetbrains.compose.runtime:runtime:$version"
    const val runtimeSaveable = "org.jetbrains.compose.runtime:runtime-saveable:$version"
  }

  const val composeIconsFontAwesome = "br.com.devsrsouza.compose.icons:font-awesome:1.1.0"

  object Coroutines {
    private const val version = "1.9.0"
    const val android = "org.jetbrains.kotlinx:kotlinx-coroutines-android:$version"
    const val core = "org.jetbrains.kotlinx:kotlinx-coroutines-core:$version"
    const val test = "org.jetbrains.kotlinx:kotlinx-coroutines-test:$version"
  }

  const val dokkaGradlePlugin = "org.jetbrains.dokka:dokka-gradle-plugin:1.8.10"

  const val essentialsGradlePlugin = "com.ivianuu.essentials:gradle-plugin:0.0.1-dev1273"

  object Firebase {
    const val ads = "com.google.firebase:firebase-ads:23.5.0"
    const val analytics = "com.google.firebase:firebase-analytics-ktx:22.1.2"
    const val crashlytics = "com.google.firebase:firebase-crashlytics-ktx:19.2.1"
    const val crashlyticsGradlePlugin = "com.google.firebase:firebase-crashlytics-gradle:3.0.2"
  }

  const val flowExt = "io.github.hoc081098:FlowExt:0.8.1-Beta"

  const val googlePlayServicesGradlePlugin = "com.google.gms:google-services:4.4.2"

  object Injekt {
    private const val version = "0.0.1-dev745"
    const val common = "com.ivianuu.injekt:common:$version"
    const val core = "com.ivianuu.injekt:core:$version"
    const val compiler = "com.ivianuu.injekt:compiler:$version"
    const val gradlePlugin = "com.ivianuu.injekt:gradle-plugin:$version"
  }

  const val junit = "junit:junit:4.13"

  const val kermit = "co.touchlab:kermit:2.0.3"

  const val kotestAssertions = "io.kotest:kotest-assertions-core:5.4.2"

  object Kotlin {
    const val version = "2.0.21"
    const val compilerEmbeddable = "org.jetbrains.kotlin:kotlin-compiler-embeddable:$version"
    const val compiler = "org.jetbrains.kotlin:kotlin-compiler:$version"
    const val gradlePlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:$version"
    const val gradlePluginApi = "org.jetbrains.kotlin:kotlin-gradle-plugin-api:$version"
  }

  object KotlinCompileTesting {
    private const val version = "0.5.0-alpha04"
    const val kotlinCompileTesting = "dev.zacsweers.kctfork:core:$version"
    const val ksp = "dev.zacsweers.kctfork:ksp:$version"
  }

  object KotlinSerialization {
    const val gradlePlugin = "org.jetbrains.kotlin:kotlin-serialization:${Kotlin.version}"
    const val json = "org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3"
  }

  object Ksp {
    const val version = "2.0.21-1.0.25"
    const val api = "com.google.devtools.ksp:symbol-processing-api:$version"
    const val gradlePlugin = "com.google.devtools.ksp:symbol-processing-gradle-plugin:$version"
    const val symbolProcessing = "com.google.devtools.ksp:symbol-processing:$version"
  }

  const val leakCanary = "com.squareup.leakcanary:leakcanary-android:2.5"

  const val materialMotionCompose = "io.github.fornewid:material-motion-compose-core:0.12.3"

  const val mockk = "io.mockk:mockk:1.12.8"

  const val molecule = "app.cash.molecule:molecule-runtime:2.0.0"

  const val mavenPublishGradlePlugin = "com.vanniktech:gradle-maven-publish-plugin:0.30.0"

  const val playBillingKtx = "com.android.billingclient:billing-ktx:7.1.1"

  const val processPhoenix = "com.jakewharton:process-phoenix:2.1.2"

  const val quiver = "app.cash.quiver:lib:0.5.0"

  const val roboelectric = "org.robolectric:robolectric:4.10.3"

  const val splittiesCoroutines = "com.louiscad.splitties:splitties-coroutines:3.0.0"

  const val superUser = "eu.chainfire:libsuperuser:1.1.1"

  const val turbine = "app.cash.turbine:turbine:0.10.0"

  const val xposed = "de.robv.android.xposed:api:82"
}
