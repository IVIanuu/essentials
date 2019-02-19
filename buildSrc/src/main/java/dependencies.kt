/*
 * Copyright 2018 Manuel Wrage
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
    const val minSdk = 21
    const val targetSdk = 28
    const val targetSdkSample = 27
    const val versionCode = 1
    const val versionName = "0.0.1"
}

object Publishing {
    const val groupId = "com.ivianuu.essentials"
    const val vcsUrl = "https://github.com/IVIanuu/essentials"
    const val version = "${Build.versionName}-dev-20"
}

object Versions {
    const val androidGradlePlugin = "3.3.0"

    const val androidxAppCompat = "1.0.2"
    const val androidxCardView = "1.0.0"
    const val androidxCore = "1.0.1"
    const val androidxLifecycle = "2.0.0"
    const val androidxWork = "1.0.0-beta03"

    const val androidxTestCore = "1.0.0"
    const val androidxTestJunit = "1.0.0"
    const val androidxTestRules = "1.1.0"
    const val androidxTestRunner = "1.1.0"

    const val bintray = "1.8.4"

    const val butterknife = "10.0.0"

    const val closeable = "0.0.1-dev-2"
    const val constraintLayout = "2.0.0-alpha3"
    const val coroutines = "1.1.0"
    const val director = "0.0.1-dev-9"
    const val epoxy = "3.0.0"
    const val epoxyKtx = "0.0.1-dev-1"
    const val epoxyPrefs = "0.0.1-dev-2"
    const val glide = "4.8.0"
    const val injekt = "0.0.1-dev-6"
    const val junit = "4.12"
    const val kommon = "0.0.1-dev-1"
    const val kotlin = "1.3.21"
    const val kPrefs = "0.0.1-dev-4"
    const val kSettings = "0.0.1-dev-3"
    const val liveEvent = "1e265df911"
    const val mavenGradle = "2.1"
    const val materialComponents = "1.1.0-alpha04"
    const val materialDialogs = "2.0.0-rc8"
    const val mockitoKotlin = "2.0.0"
    const val roboelectric = "4.0.2"
    const val rxAndroid = "2.1.0"
    const val rxJava = "2.2.6"
    const val rxJavaKtx = "0.0.1-dev-1"
    const val rxKotlin = "2.3.0"
    const val scopes = "0.0.1-dev-2"
    const val superUser = "1.0.0.+"
    const val stateStore = "0.0.1-dev-2"
    const val stdlibx = "0.0.1-dev-4"
    const val timber = "4.7.1"
    const val timberKtx = "0.0.1-dev-1"
    const val traveler = "0.0.1-dev-2"
}

object Deps {
    const val androidGradlePlugin = "com.android.tools.build:gradle:${Versions.androidGradlePlugin}"

    const val androidxAppCompat = "androidx.appcompat:appcompat:${Versions.androidxAppCompat}"
    const val androidxCardView = "androidx.cardview:cardview:${Versions.androidxCardView}"
    const val androidxCoreKtx = "androidx.core:core-ktx:${Versions.androidxCore}"
    const val androidxLifecycleExtensions =
        "androidx.lifecycle:lifecycle-extensions:${Versions.androidxLifecycle}"
    const val androidxWorkRuntime = "android.arch.work:work-runtime-ktx:${Versions.androidxWork}"

    const val androidxTestCore = "androidx.test:core:${Versions.androidxTestCore}"
    const val androidxTestJunit = "androidx.test.ext:junit:${Versions.androidxTestJunit}"
    const val androidxTestRules = "androidx.test:rules:${Versions.androidxTestRules}"
    const val androidxTestRunner = "androidx.test:runner:${Versions.androidxTestRunner}"

    const val bintrayGradlePlugin =
        "com.jfrog.bintray.gradle:gradle-bintray-plugin:${Versions.bintray}"

    const val butterknifeGradlePlugin =
        "com.jakewharton:butterknife-gradle-plugin:${Versions.butterknife}"

    const val closeable = "com.ivianuu.closeable:closeable:${Versions.closeable}"
    const val closeableCoroutines = "com.ivianuu.closeable:closeable-coroutines:${Versions.closeable}"
    const val closeableRx = "com.ivianuu.closeable:closeable-rx:${Versions.closeable}"

    const val constraintLayout =
        "androidx.constraintlayout:constraintlayout:${Versions.constraintLayout}"

    const val coroutinesAndroid =
        "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.coroutines}"
    const val coroutinesCore =
        "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.coroutines}"
    const val coroutinesRxJava =
        "org.jetbrains.kotlinx:kotlinx-coroutines-rx2:${Versions.coroutines}"

    const val director = "com.ivianuu.director:director:${Versions.director}"
    const val directorActivityCallbacks =
        "com.ivianuu.director:director-activitycallbacks:${Versions.director}"
    const val directorAndroidxLifecycle =
        "com.ivianuu.director:director-androidx-lifecycle:${Versions.director}"
    const val directorCommon = "com.ivianuu.director:director-common:${Versions.director}"
    const val directorDialog = "com.ivianuu.director:director-dialog:${Versions.director}"
    const val directorFragmentHost = "com.ivianuu.director:director-fragmenthost:${Versions.director}"
    const val directorRetained = "com.ivianuu.director:director-retained:${Versions.director}"
    const val directorScopes = "com.ivianuu.director:director-scopes:${Versions.director}"
    const val directorTesting =
        "com.ivianuu.director:director-testing:${Versions.director}"
    const val directorTraveler =
        "com.ivianuu.director:director-traveler:${Versions.director}"
    const val directorViewPager =
        "com.ivianuu.director:director-viewpager:${Versions.director}"

    const val epoxy = "com.airbnb.android:epoxy:${Versions.epoxy}"
    const val epoxyProcessor = "com.airbnb.android:epoxy-processor:${Versions.epoxy}"
    const val epoxyKtx = "com.ivianuu.epoxyktx:epoxyktx:${Versions.epoxyKtx}"
    const val epoxyPrefs = "com.ivianuu.epoxyprefs:epoxyprefs:${Versions.epoxyPrefs}"

    const val glide = "com.github.bumptech.glide:glide:${Versions.glide}"

    const val injekt = "com.ivianuu.injekt:injekt:${Versions.injekt}"
    const val injektAndroid = "com.ivianuu.injekt:injekt-android:${Versions.injekt}"
    const val injektAnnotations = "com.ivianuu.injekt:injekt-annotations:${Versions.injekt}"
    const val injektCommon = "com.ivianuu.injekt:injekt-common:${Versions.injekt}"
    const val injektCompiler = "com.ivianuu.injekt:injekt-compiler:${Versions.injekt}"
    const val injektMultiBinding =
        "com.ivianuu.injekt:injekt-multibinding:${Versions.injekt}"

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

    const val kPrefs = "com.ivianuu.kprefs:kprefs:${Versions.kPrefs}"
    const val kPrefsCoroutines = "com.ivianuu.kprefs:kprefs-coroutines:${Versions.kPrefs}"
    const val kPrefsLiveData = "com.ivianuu.kprefs:kprefs-livedata:${Versions.kPrefs}"
    const val kPrefsRx = "com.ivianuu.kprefs:kprefs-rx:${Versions.kPrefs}"

    const val kSettings =
        "com.ivianuu.ksettings:ksettings:${Versions.kSettings}"
    const val kSettingsCoroutines =
        "com.ivianuu.ksettings:ksettings-coroutines:${Versions.kSettings}"
    const val kSettingsLiveData =
        "com.ivianuu.ksettings:ksettings-livedata:${Versions.kSettings}"
    const val kSettingsRx =
        "com.ivianuu.ksettings:ksettings-rx:${Versions.kSettings}"

    const val liveEvent = "com.github.IVIanuu:liveevent:${Versions.liveEvent}"

    const val materialComponents =
        "com.google.android.material:material:${Versions.materialComponents}"

    const val materialDialogsCore =
        "com.afollestad.material-dialogs:core:${Versions.materialDialogs}"
    const val materialDialogsInput =
        "com.afollestad.material-dialogs:input:${Versions.materialDialogs}"
    const val materialDialogsColor =
        "com.afollestad.material-dialogs:color:${Versions.materialDialogs}"

    const val mavenGradlePlugin = "com.github.dcendents:android-maven-gradle-plugin:${Versions.mavenGradle}"

    const val mockitoKotlin = "com.nhaarman.mockitokotlin2:mockito-kotlin:${Versions.mockitoKotlin}"

    const val roboelectric = "org.robolectric:robolectric:${Versions.roboelectric}"

    const val rxAndroid = "io.reactivex.rxjava2:rxandroid:${Versions.rxAndroid}"
    const val rxJava = "io.reactivex.rxjava2:rxjava:${Versions.rxJava}"
    const val rxJavaKtx = "com.ivianuu.rxjavaktx:rxjavaktx:${Versions.rxJavaKtx}"
    const val rxKotlin = "io.reactivex.rxjava2:rxkotlin:${Versions.rxKotlin}"

    const val scopes = "com.ivianuu.scopes:scopes:${Versions.scopes}"
    const val scopesAndroid = "com.ivianuu.scopes:scopes-android:${Versions.scopes}"
    const val scopesAndroidLifecycle =
        "com.ivianuu.scopes:scopes-android-lifecycle:${Versions.scopes}"
    const val scopesCommon = "com.ivianuu.scopes:scopes-common:${Versions.scopes}"
    const val scopesCoroutines = "com.ivianuu.scopes:scopes-coroutines:${Versions.scopes}"
    const val scopesLiveData =
        "com.ivianuu.scopes:scopes-livedata:${Versions.scopes}"
    const val scopesRx = "com.ivianuu.scopes:scopes-rx:${Versions.scopes}"

    const val stateStore = "com.ivianuu.statestore:statestore:${Versions.stateStore}"
    const val stateStoreAndroid =
        "com.ivianuu.statestore:statestore-android:${Versions.stateStore}"
    const val stateStoreCoroutines =
        "com.ivianuu.statestore:statestore-coroutines:${Versions.stateStore}"
    const val stateStoreLiveData =
        "com.ivianuu.statestore:statestore-livedata:${Versions.stateStore}"
    const val stateStoreRx = "com.ivianuu.statestore:statestore-rx:${Versions.stateStore}"

    const val stdlibx = "com.ivianuu.stdlibx:stdlibx:${Versions.stdlibx}"

    const val superUser = "eu.chainfire:libsuperuser:${Versions.superUser}"

    const val timber = "com.jakewharton.timber:timber:${Versions.timber}"
    const val timberKtx = "com.ivianuu.timberktx:timberktx:${Versions.timberKtx}"

    const val traveler = "com.ivianuu.traveler:traveler:${Versions.traveler}"
    const val travelerAndroid = "com.ivianuu.traveler:traveler-android:${Versions.traveler}"
    const val travelerCommon =
        "com.ivianuu.traveler:traveler-common:${Versions.traveler}"
    const val travelerLifecycle =
        "com.ivianuu.traveler:traveler-lifecycle:${Versions.traveler}"
}