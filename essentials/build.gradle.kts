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
plugins {
    id("com.android.library")
    id("com.jakewharton.butterknife")
    id("kotlin-android")
    id("kotlin-kapt")
}

apply(from = "https://raw.githubusercontent.com/IVIanuu/gradle-scripts/master/android-build-lib.gradle")
apply(from = "https://raw.githubusercontent.com/IVIanuu/gradle-scripts/master/kt-android-ext.gradle")
apply(from = "https://raw.githubusercontent.com/IVIanuu/gradle-scripts/master/kt-kapt.gradle")

android {
    defaultConfig {
        consumerProguardFile("proguard-rules.txt")
    }
}

apply(from = "https://raw.githubusercontent.com/IVIanuu/gradle-scripts/master/mvn-publish.gradle")

dependencies {
    api(Deps.androidxAppCompat)
    api(Deps.androidxCardView)
    api(Deps.androidxCoreKtx)

    api(Deps.closeable)
    api(Deps.closeableCoroutines)
    api(Deps.closeableRx)

    api(Deps.coroutinesAndroid)
    api(Deps.coroutinesCore)
    api(Deps.coroutinesRxJava)

    api(Deps.androidxLifecycleExtensions)

    api(Deps.constraintLayout)

    api(Deps.director)
    api(Deps.directorActivityCallbacks)
    api(Deps.directorAndroidxLifecycle)
    api(Deps.directorCommon)
    api(Deps.directorDialog)
    api(Deps.directorFragmentHost)
    api(Deps.directorScopes)
    api(Deps.directorTraveler)
    api(Deps.directorViewPager)

    api(Deps.epoxy)
    api(Deps.epoxyKtx)
    api(Deps.epoxyPrefs)

    kapt(project(":essentials-compiler"))

    api(Deps.injekt)
    api(Deps.injektAndroid)
    api(Deps.injektAnnotations)
    api(Deps.injektCommon)
    api(Deps.injektMultiBinding)

    api(Deps.kommonAppCompat)
    api(Deps.kommonCore)
    api(Deps.kommonLifecycle)
    api(Deps.kommonMaterial)
    api(Deps.kommonRecyclerView)
    api(Deps.kommonViewPager)

    api(Deps.kotlinStdLib)

    api(Deps.kPrefs)
    api(Deps.kPrefsCommon)
    api(Deps.kPrefsCoroutines)
    api(Deps.kPrefsLiveData)
    api(Deps.kPrefsRx)

    api(Deps.kSettings)
    api(Deps.kSettingsCoroutines)
    api(Deps.kSettingsLiveData)
    api(Deps.kSettingsRx)

    api(project(":ktuples"))

    api(Deps.liveEvent)

    api(Deps.materialComponents)

    api(Deps.materialDialogsCore)

    api(Deps.rxAndroid)
    api(Deps.rxJava)
    api(Deps.rxJavaKtx)
    api(Deps.rxKotlin)

    api(Deps.scopes)
    api(Deps.scopesAndroid)
    api(Deps.scopesAndroidLifecycle)
    api(Deps.scopesCommon)
    api(Deps.scopesCoroutines)
    api(Deps.scopesRx)

    api(Deps.stateStore)
    api(Deps.stateStoreAndroid)
    api(Deps.stateStoreCoroutines)
    api(Deps.stateStoreLiveData)
    api(Deps.stateStoreRx)

    api(Deps.stdlibx)

    api(Deps.timber)
    api(Deps.timberKtx)

    api(Deps.traveler)
    api(Deps.travelerAndroid)
    api(Deps.travelerLifecycle)
}