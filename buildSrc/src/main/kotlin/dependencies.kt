/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

@file:Suppress("ClassName", "unused")

object Build {
  const val applicationId = "essentials.sample"
  const val compileSdk = 35
  const val minSdk = 28
  const val targetSdk = 31
  const val versionCode = 1
  const val versionName = "0.0.1"
}

object Deps {
  const val androidGradlePlugin = "com.android.tools.build:gradle:8.6.0"

  object AndroidX {
    object Activity {
      private const val version = "1.10.0"
      const val activity = "androidx.activity:activity:$version"
      const val compose = "androidx.activity:activity-compose:$version"
    }

    const val core = "androidx.core:core-ktx:1.9.0"

    object DataStore {
      private const val version = "1.1.3"
      const val dataStore = "androidx.datastore:datastore:$version"
      const val preferences = "androidx.datastore:datastore-preferences:$version"
    }

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
    private const val version = "1.2.4"
    const val atomic = "io.arrow-kt:arrow-atomic:$version"
    const val core = "io.arrow-kt:arrow-core:$version"
    const val fxCoroutines = "io.arrow-kt:arrow-fx-coroutines:$version"
    const val resilience = "io.arrow-kt:arrow-resilience:$version"
  }

  object AtomicFu {
    private const val version = "0.27.0"
    const val gradlePlugin = "org.jetbrains.kotlinx:atomicfu-gradle-plugin:$version"
    const val runtime = "org.jetbrains.kotlinx:atomicfu:$version"
  }

  object AutoService {
    private const val version = "1.1.0"
    const val annotations = "com.google.auto.service:auto-service-annotations:$version"
    const val processor = "dev.zacsweers.autoservice:auto-service-ksp:$version"
  }

  const val circuitFoundation = "com.slack.circuit:circuit-foundation:0.25.0"

  object Coil {
    private const val version = "2.2.1"
    const val coil = "io.coil-kt:coil:$version"
    const val coilCompose = "io.coil-kt:coil-compose:$version"
  }

  object Compose {
    const val version = "1.8.0-rc02"
    const val gradlePlugin = "org.jetbrains.kotlin:compose-compiler-gradle-plugin:${Kotlin.version}"
    const val foundation = "androidx.compose.foundation:foundation:$version"
    const val googleFonts = "androidx.compose.ui:ui-text-google-fonts:$version"
    const val material3 = "androidx.compose.material3:material3:1.3.1"
    const val materialIconsExtended = "androidx.compose.material:material-icons-extended:1.7.8"
    const val runtime = "androidx.compose.runtime:runtime:$version"
    const val runtimeSaveable = "androidx.compose.runtime:runtime-saveable:$version"
  }

  const val composeIconsFontAwesome = "br.com.devsrsouza.compose.icons:font-awesome:1.1.0"
  const val composeReordable = "sh.calvin.reorderable:reorderable:2.4.3"

  object Coroutines {
    private const val version = "1.10.1"
    const val android = "org.jetbrains.kotlinx:kotlinx-coroutines-android:$version"
    const val core = "org.jetbrains.kotlinx:kotlinx-coroutines-core:$version"
    const val test = "org.jetbrains.kotlinx:kotlinx-coroutines-test:$version"
  }

  const val dokkaGradlePlugin = "org.jetbrains.dokka:dokka-gradle-plugin:2.0.0"

  const val essentialsGradlePlugin = "io.github.ivianuu.essentials:gradle-plugin:0.0.1-dev1288"

  object Firebase {
    const val ads = "com.google.firebase:firebase-ads:23.5.0"
    const val analytics = "com.google.firebase:firebase-analytics-ktx:22.1.2"
    const val crashlytics = "com.google.firebase:firebase-crashlytics-ktx:19.2.1"
    const val crashlyticsGradlePlugin = "com.google.firebase:firebase-crashlytics-gradle:3.0.2"
  }

  const val flowExt = "io.github.hoc081098:FlowExt:0.8.1-Beta"

  const val googlePlayServicesGradlePlugin = "com.google.gms:google-services:4.4.2"

  object Injekt {
    private const val version = "0.0.1-dev754"
    const val common = "io.github.ivianuu.injekt:common:$version"
    const val core = "io.github.ivianuu.injekt:core:$version"
    const val compiler = "io.github.ivianuu.injekt:compiler:$version"
    const val gradlePlugin = "io.github.ivianuu.injekt:gradle-plugin:$version"
  }

  const val junit = "junit:junit:4.13"

  const val kermit = "co.touchlab:kermit:2.0.3"

  const val kotestAssertions = "io.kotest:kotest-assertions-core:5.4.2"

  object Kotlin {
    const val version = "2.1.20"
    const val compilerEmbeddable = "org.jetbrains.kotlin:kotlin-compiler-embeddable:$version"
    const val compiler = "org.jetbrains.kotlin:kotlin-compiler:$version"
    const val gradlePlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:$version"
    const val gradlePluginApi = "org.jetbrains.kotlin:kotlin-gradle-plugin-api:$version"
  }

  object KotlinCompileTesting {
    private const val version = "0.7.0"
    const val kotlinCompileTesting = "dev.zacsweers.kctfork:core:$version"
    const val ksp = "dev.zacsweers.kctfork:ksp:$version"
  }

  object KotlinSerialization {
    const val gradlePlugin = "org.jetbrains.kotlin:kotlin-serialization:${Kotlin.version}"
    const val json = "org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.0"
  }

  const val kotlinResult = "com.michael-bull.kotlin-result:kotlin-result:2.0.1"

  object Ksp {
    const val version = "2.1.20-1.0.31"
    const val api = "com.google.devtools.ksp:symbol-processing-api:$version"
    const val gradlePlugin = "com.google.devtools.ksp:symbol-processing-gradle-plugin:$version"
    const val symbolProcessing = "com.google.devtools.ksp:symbol-processing:$version"
  }

  const val leakCanary = "com.squareup.leakcanary:leakcanary-android:2.5"

  const val materialKolor = "com.materialkolor:material-kolor:2.1.1"
  const val materialMotionCompose = "io.github.fornewid:material-motion-compose-core:2.0.1"

  const val mockk = "io.mockk:mockk:1.12.8"

  const val molecule = "app.cash.molecule:molecule-runtime:2.1.0"

  const val mavenPublishGradlePlugin = "com.vanniktech:gradle-maven-publish-plugin:0.30.0"

  const val playBillingKtx = "com.android.billingclient:billing-ktx:7.1.1"

  const val processPhoenix = "com.jakewharton:process-phoenix:2.1.2"

  const val roboelectric = "org.robolectric:robolectric:4.10.3"

  const val splittiesCoroutines = "com.louiscad.splitties:splitties-coroutines:3.0.0"

  const val superUser = "eu.chainfire:libsuperuser:1.1.1"

  const val turbine = "app.cash.turbine:turbine:0.10.0"

  const val xposed = "de.robv.android.xposed:api:82"
}
