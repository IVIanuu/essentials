/*
 * Copyright 2020 Manuel Wrage
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
    const val compileSdk = 29
    const val minSdk = 24
    const val targetSdk = 29
    const val versionCode = 1
    const val versionName = "0.0.1"
}

object Publishing {
    const val groupId = "com.ivianuu.essentials"
    const val vcsUrl = "https://github.com/IVIanuu/essentials"
    const val version = "${Build.versionName}-dev704"
}

object Deps {
    const val accompanistCoil = "dev.chrisbanes.accompanist:accompanist-coil:0.3.2"
    const val androidGradlePlugin = "com.android.tools.build:gradle:4.0.1"

    object AndroidX {
        const val appCompat = "androidx.appcompat:appcompat:1.1.0"
        const val core = "androidx.core:core-ktx:1.5.0-alpha02"

        object Compose {
            const val version = "1.0.0-alpha06"

            const val compiler = "androidx.compose.compiler:compiler:$version"
            const val core = "androidx.compose.ui:ui:$version"
            const val material = "androidx.compose.material:material:$version"
            const val runtime = "androidx.compose.runtime:runtime:$version"
            const val test = "androidx.compose.test:test-core:$version"
        }

        object Lifecycle {
            private const val version = "2.2.0"
            const val extensions = "androidx.lifecycle:lifecycle-extensions:$version"
            const val runtime = "androidx.lifecycle:lifecycle-runtime-ktx:$version"
        }

        object Test {
            const val core = "androidx.test:core:1.2.0"
            const val junit = "androidx.test.ext:junit:1.1.1"
            const val rules = "androidx.test:rules:1.2.0"
            const val runner = "androidx.test:runner:1.2.0"
        }

        const val work = "androidx.work:work-runtime-ktx:2.1.0"
    }

    const val autoService = "com.google.auto.service:auto-service:1.0-rc6"

    const val bintrayGradlePlugin = "com.jfrog.bintray.gradle:gradle-bintray-plugin:1.8.5"

    const val buildConfigGradlePlugin =
        "gradle.plugin.de.fuerstenau:BuildConfigPlugin:1.1.8"

    const val coil = "io.coil-kt:coil:1.0.0"

    const val compileTesting = "com.github.tschuchortdev:kotlin-compile-testing:1.2.7"

    object Coroutines {
        private const val version = "1.4.0"
        const val android = "org.jetbrains.kotlinx:kotlinx-coroutines-android:$version"
        const val core = "org.jetbrains.kotlinx:kotlinx-coroutines-core:$version"
        const val test = "org.jetbrains.kotlinx:kotlinx-coroutines-test:$version"
    }

    const val dexcountGradlePlugin = "com.getkeepsafe.dexcount:dexcount-gradle-plugin:1.0.0"

    const val essentialsGradlePlugin =
        "com.ivianuu.essentials:essentials-gradle-plugin:0.0.1-dev703"

    const val firebaseCrashlytics = "com.google.firebase:firebase-crashlytics:17.2.2"

    object Injekt {
        private const val version = "0.0.1-dev400"
        const val android = "com.ivianuu.injekt:injekt-android:$version"
        const val androidWork = "com.ivianuu.injekt:injekt-android-work:$version"
        const val core = "com.ivianuu.injekt:injekt-core:$version"
        const val merge = "com.ivianuu.injekt:injekt-merge:$version"
        const val gradlePlugin = "com.ivianuu.injekt:injekt-gradle-plugin:$version"
    }

    const val junit = "junit:junit:4.13"

    const val kotestAssertions = "io.kotest:kotest-assertions-core:4.3.0"

    object Kotlin {
        const val version = "1.4.255-SNAPSHOT"
        const val compilerEmbeddable = "org.jetbrains.kotlin:kotlin-compiler-embeddable:$version"
        const val compiler = "org.jetbrains.kotlin:kotlin-compiler:$version"
        const val gradlePlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:$version"
        const val gradlePluginApi = "org.jetbrains.kotlin:kotlin-gradle-plugin-api:$version"
        const val stdlib = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:$version"
    }

    const val mockk = "io.mockk:mockk:1.10.0"

    object Moshi {
        private const val version = "1.9.2"
        const val adapters = "com.squareup.moshi:moshi-adapters:$version"
        const val moshi = "com.squareup.moshi:moshi:$version"
        const val codegen = "com.squareup.moshi:moshi-kotlin-codegen:$version"
    }

    object MoshiSealed {
        private const val version = "0.1.0"
        const val annotations = "dev.zacsweers.moshisealed:moshi-sealed-annotations:$version"
        const val codegen = "dev.zacsweers.moshisealed:moshi-sealed-codegen:$version"
    }

    const val mavenGradlePlugin = "com.github.dcendents:android-maven-gradle-plugin:2.1"

    const val playBilling = "com.android.billingclient:billing-ktx:2.1.0"

    const val processingX = "com.ivianuu.processingx:processingx:0.0.1-dev4"

    const val roboelectric = "org.robolectric:robolectric:4.3.1"

    const val spotlessGradlePlugin = "com.diffplug.spotless:spotless-plugin-gradle:3.26.1"

    const val superUser = "eu.chainfire:libsuperuser:1.0.0.+"

    const val versionsGradlePlugin = "com.github.ben-manes:gradle-versions-plugin:0.27.0"
}
