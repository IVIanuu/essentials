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

    const val compileSdk = 28
    const val minSdk = 23
    const val targetSdk = 28
    const val targetSdkSample = 29
    const val versionCode = 1
    const val versionName = "0.0.1"
}

object Publishing {
    const val groupId = "com.ivianuu.essentials"
    const val vcsUrl = "https://github.com/IVIanuu/essentials"
    const val version = "${Build.versionName}-dev178"
}

object Versions {
    const val androidGradlePlugin = "3.5.0-rc01"

    const val androidxActivity = "1.0.0-rc01"
    const val androidxAppCompat = "1.1.0-rc01"
    const val androidxCardView = "1.0.0"
    const val androidxCore = "1.1.0-rc02"
    const val androidxFragment = "1.1.0-rc01"
    const val androidxLifecycle = "2.2.0-alpha02"
    const val androidxLifecycleViewModelSavedState = "1.0.0-alpha02"
    const val androidxWork = "2.1.0-rc01"

    const val androidxTestCore = "1.2.0-alpha03"
    const val androidxTestJunit = "1.1.1-alpha03"
    const val androidxTestRules = "1.2.0-alpha03"
    const val androidxTestRunner = "1.2.0-alpha03"

    const val bintray = "1.8.4"
    const val compose = "0.0.1-dev35"
    const val constraintLayout = "2.0.0-beta2"
    const val coroutines = "1.3.0-RC"
    const val glide = "4.9.0"
    const val injekt = "0.0.1-dev70"
    const val junit = "4.12"
    const val kommon = "0.0.1-dev13" // remove
    const val kotlin = "1.3.41"
    const val kotlinFlowExtensions = "0.0.2"
    const val kPrefs = "0.0.1-dev16"
    const val kSettings = "0.0.1-dev11"
    const val mavenGradle = "2.1"
    const val materialComponents = "1.1.0-alpha07"
    const val materialDialogs = "3.1.0"
    const val mockitoKotlin = "2.0.0"
    const val roboelectric = "4.0.2"
    const val scopes = "0.0.1-dev26"
    const val superUser = "1.0.0.+"
    const val timber = "4.7.1"
    const val timberKt = "1.5.1"
}

object Deps {
    const val androidGradlePlugin = "com.android.tools.build:gradle:${Versions.androidGradlePlugin}"

    const val androidxActivity = "androidx.activity:activity-ktx:${Versions.androidxActivity}"
    const val androidxAppCompat = "androidx.appcompat:appcompat:${Versions.androidxAppCompat}"
    const val androidxCardView = "androidx.cardview:cardview:${Versions.androidxCardView}"
    const val androidxCore = "androidx.core:core-ktx:${Versions.androidxCore}"
    const val androidxLifecycleLiveData =
        "androidx.lifecycle:lifecycle-livedata-ktx:${Versions.androidxLifecycle}"
    const val androidxLifecycleReactiveStreams =
        "androidx.lifecycle:lifecycle-reactivestreams-ktx:${Versions.androidxLifecycle}"

    const val androidxLifecycleRuntime =
        "androidx.lifecycle:lifecycle-runtime-ktx:${Versions.androidxLifecycle}"
    const val androidxLifecycleViewModel =
        "androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.androidxLifecycle}"
    const val androidxLifecycleViewModelSavedState =
        "androidx.lifecycle:lifecycle-viewmodel-savedstate:${Versions.androidxLifecycleViewModelSavedState}"
    const val androidxWork = "androidx.work:work-runtime-ktx:${Versions.androidxWork}"

    const val androidxTestCore = "androidx.test:core:${Versions.androidxTestCore}"
    const val androidxTestJunit = "androidx.test.ext:junit:${Versions.androidxTestJunit}"
    const val androidxTestRules = "androidx.test:rules:${Versions.androidxTestRules}"
    const val androidxTestRunner = "androidx.test:runner:${Versions.androidxTestRunner}"

    const val bintrayGradlePlugin =
        "com.jfrog.bintray.gradle:gradle-bintray-plugin:${Versions.bintray}"

    const val compose = "com.ivianuu.compose:compose:${Versions.compose}"
    const val composeCommon = "com.ivianuu.compose:compose-common:${Versions.compose}"
    const val composeGradlePlugin = "com.ivianuu.compose:compose-gradle-plugin:${Versions.compose}"

    const val constraintLayout =
        "androidx.constraintlayout:constraintlayout:${Versions.constraintLayout}"

    const val coroutinesAndroid =
        "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.coroutines}"
    const val coroutinesCore =
        "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.coroutines}"
    const val coroutinesTest =
        "org.jetbrains.kotlinx:kotlinx-coroutines-test:${Versions.coroutines}"

    const val glide = "com.github.bumptech.glide:glide:${Versions.glide}"

    const val injekt = "com.ivianuu.injekt:injekt:${Versions.injekt}"
    const val injektAndroid = "com.ivianuu.injekt:injekt-android:${Versions.injekt}"
    const val injektCompiler = "com.ivianuu.injekt:injekt-compiler:${Versions.injekt}"

    const val junit = "junit:junit:${Versions.junit}"

    const val kommonAppCompat =
        "com.ivianuu.kommon:kommon-appcompat:${Versions.kommon}"
    const val kommonCore = "com.ivianuu.kommon:kommon-core:${Versions.kommon}"
    const val kommonLifecycle =
        "com.ivianuu.kommon:kommon-lifecycle:${Versions.kommon}"
    const val kommonMaterial =
        "com.ivianuu.kommon:kommon-material:${Versions.kommon}"
    const val kommonRecyclerView =
        "com.ivianuu.kommon:kommon-recyclerview:${Versions.kommon}"
    const val kommonViewPager =
        "com.ivianuu.kommon:kommon-viewpager:${Versions.kommon}"

    const val kotlinGradlePlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin}"
    const val kotlinStdLib = "org.jetbrains.kotlin:kotlin-stdlib-jdk7:${Versions.kotlin}"

    const val kotlinFlowExtensions =
        "com.github.akarnokd:kotlin-flow-extensions:${Versions.kotlinFlowExtensions}"

    const val kPrefs = "com.ivianuu.kprefs:kprefs:${Versions.kPrefs}"
    const val kPrefsCommon = "com.ivianuu.kprefs:kprefs-common:${Versions.kPrefs}"
    const val kPrefsCoroutines = "com.ivianuu.kprefs:kprefs-coroutines:${Versions.kPrefs}"
    const val kPrefsLiveData = "com.ivianuu.kprefs:kprefs-livedata:${Versions.kPrefs}"

    const val kSettings =
        "com.ivianuu.ksettings:ksettings:${Versions.kSettings}"
    const val kSettingsCoroutines =
        "com.ivianuu.ksettings:ksettings-coroutines:${Versions.kSettings}"
    const val kSettingsLiveData =
        "com.ivianuu.ksettings:ksettings-livedata:${Versions.kSettings}"

    const val materialComponents =
        "com.google.android.material:material:${Versions.materialComponents}"

    const val materialDialogsCore =
        "com.afollestad.material-dialogs:core:${Versions.materialDialogs}"
    const val materialDialogsBottomSheets =
        "com.afollestad.material-dialogs:bottomsheets:${Versions.materialDialogs}"
    const val materialDialogsColor =
        "com.afollestad.material-dialogs:color:${Versions.materialDialogs}"
    const val materialDialogsDateTime =
        "com.afollestad.material-dialogs:datetime:${Versions.materialDialogs}"
    const val materialDialogsFiles =
        "com.afollestad.material-dialogs:files:${Versions.materialDialogs}"
    const val materialDialogsInput =
        "com.afollestad.material-dialogs:input:${Versions.materialDialogs}"
    const val materialDialogsLifecycle =
        "com.afollestad.material-dialogs:lifecycle:${Versions.materialDialogs}"

    const val mavenGradlePlugin =
        "com.github.dcendents:android-maven-gradle-plugin:${Versions.mavenGradle}"

    const val mockitoKotlin = "com.nhaarman.mockitokotlin2:mockito-kotlin:${Versions.mockitoKotlin}"

    const val roboelectric = "org.robolectric:robolectric:${Versions.roboelectric}"

    const val scopes = "com.ivianuu.scopes:scopes:${Versions.scopes}"
    const val scopesAndroid = "com.ivianuu.scopes:scopes-android:${Versions.scopes}"
    const val scopesCoroutines = "com.ivianuu.scopes:scopes-coroutines:${Versions.scopes}"

    const val superUser = "eu.chainfire:libsuperuser:${Versions.superUser}"

    const val timber = "com.jakewharton.timber:timber:${Versions.timber}"
    const val timberKt = "com.github.ajalt:timberkt:${Versions.timberKt}"
}