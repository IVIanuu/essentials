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
    id("com.ivianuu.essentials")
    kotlin("android")
}

apply(from = "https://raw.githubusercontent.com/IVIanuu/gradle-scripts/master/android-build-lib.gradle")
apply(from = "https://raw.githubusercontent.com/IVIanuu/gradle-scripts/master/java-8-android.gradle")
apply(from = "https://raw.githubusercontent.com/IVIanuu/gradle-scripts/master/kt-android-ext.gradle")
apply(from = "https://raw.githubusercontent.com/IVIanuu/gradle-scripts/master/kt-compiler-args.gradle")
//apply(from = "https://raw.githubusercontent.com/IVIanuu/gradle-scripts/master/kt-lint.gradle")
apply(from = "https://raw.githubusercontent.com/IVIanuu/gradle-scripts/master/kt-source-sets-android.gradle")
apply(from = "https://raw.githubusercontent.com/IVIanuu/gradle-scripts/master/mvn-publish.gradle")

dependencies {
    api(Deps.AndroidX.appCompat)
    api(Deps.AndroidX.core)
    api(Deps.AndroidX.Lifecycle.extensions)
    api(Deps.AndroidX.Lifecycle.runtime)

    api(Deps.AndroidX.Compose.runtime)
    api(Deps.AndroidX.Ui.animation)
    api(Deps.AndroidX.Ui.core)
    api(Deps.AndroidX.Ui.foundation)
    api(Deps.AndroidX.Ui.layout)
    api(Deps.AndroidX.Ui.material)
    api(Deps.AndroidX.Ui.materialIconsExtended)
    api(Deps.AndroidX.Ui.text)
    api(Deps.AndroidX.Ui.tooling)
    api(Deps.AndroidX.Ui.vector)

    api(Deps.Coroutines.android)

    api(Deps.Injekt.android)

    api(project(":essentials-core"))
    api(project(":essentials-moshi-android"))
    api(project(":essentials-store-android"))
}
