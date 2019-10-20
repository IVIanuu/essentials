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

plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
}

apply(from = "https://raw.githubusercontent.com/IVIanuu/gradle-scripts/master/android-build-lib.gradle")
apply(from = "https://raw.githubusercontent.com/IVIanuu/gradle-scripts/master/java-8-android.gradle")
apply(from = "https://raw.githubusercontent.com/IVIanuu/gradle-scripts/master/kt-android-ext.gradle")
apply(from = "https://raw.githubusercontent.com/IVIanuu/gradle-scripts/master/kt-compiler-args.gradle")
apply(from = "https://raw.githubusercontent.com/IVIanuu/gradle-scripts/master/kt-kapt.gradle")
apply(from = "https://raw.githubusercontent.com/IVIanuu/gradle-scripts/master/kt-source-sets-android.gradle")
apply(from = "https://raw.githubusercontent.com/IVIanuu/gradle-scripts/master/mvn-publish.gradle")

dependencies {
    api(Deps.androidxActivity)
    api(Deps.androidxAppCompat)
    api(Deps.androidxCardView)
    api(Deps.androidxCore)
    api(Deps.androidxLifecycleLiveData)
    api(Deps.androidxLifecycleReactiveStreams)
    api(Deps.androidxLifecycleRuntime)
    api(Deps.androidxLifecycleViewModel)
    api(Deps.androidxLifecycleViewModelSavedState)

    api(Deps.coroutinesAndroid)
    api(Deps.coroutinesCore)

    api(Deps.androidxConstraintLayout)

    api(Deps.director)
    api(Deps.directorCommon)

    api(Deps.epoxy)
    api(Deps.epoxyPrefs)

    kapt(project(":essentials-compiler"))

    api(Deps.injekt)
    api(Deps.injektAndroid)

    api(Deps.kotlinStdLib)

    api(Deps.kotlinFlowExtensions)

    api(Deps.kPrefs)
    api(Deps.kPrefsCommon)
    api(Deps.kPrefsCoroutines)

    api(Deps.kSettings)
    api(Deps.kSettingsCoroutines)

    api(Deps.materialComponents)

    api(Deps.materialDialogsCore)
    api(Deps.materialDialogsBottomSheets)
    api(Deps.materialDialogsColor)
    api(Deps.materialDialogsDateTime)
    api(Deps.materialDialogsFiles)
    api(Deps.materialDialogsInput)
    api(Deps.materialDialogsLifecycle)

    api(Deps.scopes)
    api(Deps.scopesAndroid)
    api(Deps.scopesCoroutines)

    api(Deps.timber)
    api(Deps.timberKt)

}