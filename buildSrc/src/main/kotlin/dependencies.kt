/*
 * Copyright 2019 Manuel Wrage
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
    const val buildToolsVersion = "28.0.3"

    const val compileSdk = 29
    const val minSdk = 23
    const val targetSdk = 29
    const val versionCode = 1
    const val versionName = "0.0.1"
}

object Publishing {
    const val groupId = "com.ivianuu.essentials"
    const val vcsUrl = "https://github.com/IVIanuu/essentials"
    const val version = "${Build.versionName}-dev256"
}

object Deps {
    const val androidGradlePlugin = "com.android.tools.build:gradle:3.5.2"

    object AndroidX {
        const val activity = "androidx.activity:activity-ktx:1.1.0-rc02"
        const val appCompat = "androidx.appcompat:appcompat:1.1.0"
        const val cardView = "androidx.cardview:cardview:1.0.0"
        const val constraintLayout = "androidx.constraintlayout:constraintlayout:2.0.0-beta3"
        const val core = "androidx.core:core-ktx:1.2.0-beta02"

        object Lifecycle {
            private const val version = "2.2.0-rc02"
            const val runtime = "androidx.lifecycle:lifecycle-runtime-ktx:$version"
            const val liveData = "androidx.lifecycle:lifecycle-livedata-ktx:$version"
            const val reactiveStreams = "androidx.lifecycle:lifecycle-reactivestreams-ktx:$version"

            object ViewModel {
                const val viewModel = "androidx.lifecycle:lifecycle-viewmodel-ktx:$version"
                const val savedState =
                    "androidx.lifecycle:lifecycle-viewmodel-savedstate:1.0.0-rc02"
            }
        }

        object Test {
            const val core = "androidx.test:core:1.2.0"
            const val junit = "androidx.test.ext:junit:1.1.1"
            const val rules = "androidx.test:rules:1.2.0"
            const val runner = "androidx.test:runner:1.2.0"
        }

        object Ui {
            private const val version = "0.0.1-dev05"
            const val androidText = "androidx.ui:ui-android-text:$version"
            const val androidView = "androidx.ui:ui-android-view:$version"
            const val androidViewNonIr = "androidx.ui:ui-android-view-non-ir:$version"
            const val animation = "androidx.ui:ui-animation:$version"
            const val core = "androidx.ui:ui-core:$version"
            const val foundation = "androidx.ui:ui-foundation:$version"
            const val framework = "androidx.ui:ui-framework:$version"
            const val layout = "androidx.ui:ui-layout:$version"
            const val material = "androidx.ui:ui-material:$version"
            const val platform = "androidx.ui:ui-platform:$version"
            const val test = "androidx.ui:ui-test:$version"
            const val text = "androidx.ui:ui-text:$version"
            const val tooling = "androidx.ui:ui-tooling:$version"
            const val vector = "androidx.ui:ui-vector:$version"
        }

        const val work = "androidx.work:work-runtime-ktx:2.1.0"
    }

    const val autoService = "com.google.auto.service:auto-service:1.0-rc6"

    const val bintrayGradlePlugin = "com.jfrog.bintray.gradle:gradle-bintray-plugin:1.8.4"

    const val buildConfigGradlePlugin =
        "gradle.plugin.de.fuerstenau:BuildConfigPlugin:1.1.8"

    const val coil = "io.coil-kt:coil:0.7.0"

    object Coroutines {
        private const val version = "1.3.2"
        const val android = "org.jetbrains.kotlinx:kotlinx-coroutines-android:$version"
        const val core = "org.jetbrains.kotlinx:kotlinx-coroutines-core:$version"
        const val test = "org.jetbrains.kotlinx:kotlinx-coroutines-test:$version"
    }

    const val dexcountGradlePlugin = "com.getkeepsafe.dexcount:dexcount-gradle-plugin:1.0.0"

    object Director {
        private const val version = "0.0.1-dev40"
        const val director = "com.ivianuu.director:director:$version"
        const val common = "com.ivianuu.director:director-common:$version"
    }

    const val epoxy = "com.airbnb.android:epoxy:3.8.0"

    const val epoxyPrefs = "com.ivianuu.epoxyprefs:epoxyprefs:0.0.1-dev-21"

    const val essentialsGradlePlugin =
        "com.ivianuu.essentials:essentials-gradle-plugin:0.0.1-dev251"

    object Injekt {
        private const val version = "0.0.1-dev78"
        const val injekt = "com.ivianuu.injekt:injekt:$version"
        const val android = "com.ivianuu.injekt:injekt-android:$version"
        const val gradlePlugin = "com.ivianuu.injekt:injekt-gradle-plugin:$version"
    }

    const val junit = "junit:junit:4.12"

    object Kotlin {
        private const val version = "1.3.61"
        const val compilerEmbeddable = "org.jetbrains.kotlin:kotlin-compiler-embeddable:$version"
        const val gradlePlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:$version"
        const val gradlePluginApi = "org.jetbrains.kotlin:kotlin-gradle-plugin-api:$version"
        const val stdlib = "org.jetbrains.kotlin:kotlin-stdlib-jdk7:$version"
    }

    const val kotlinFlowExtensions = "com.github.akarnokd:kotlin-flow-extensions:0.0.2"

    const val materialComponents = "com.google.android.material:material:1.1.0-beta01"

    const val mavenGradlePlugin = "com.github.dcendents:android-maven-gradle-plugin:2.1"

    const val mockitoKotlin = "com.nhaarman.mockitokotlin2:mockito-kotlin:2.0.0"

    const val processingX = "com.ivianuu.processingx:processingx:0.0.1-dev4"

    const val roboelectric = "org.robolectric:robolectric:4.0.2"

    object Scopes {
        private const val version = "0.0.1-dev26"
        const val scopes = "com.ivianuu.scopes:scopes:$version"
        const val android = "com.ivianuu.scopes:scopes-android:$version"
        const val coroutines = "com.ivianuu.scopes:scopes-coroutines:$version"
    }

    const val superUser = "eu.chainfire:libsuperuser:1.0.0.+"

    const val timberKt = "com.github.ajalt:timberkt:1.5.1"

    const val versionsGradlePlugin = "com.github.ben-manes:gradle-versions-plugin:0.27.0"
}