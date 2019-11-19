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
    id("com.android.application")
    id("com.ivianuu.essentials.kotlin.compiler")
    kotlin("android")
    //kotlin("kapt")
    id("kotlin-android-extensions")
}

apply(from = "https://raw.githubusercontent.com/IVIanuu/gradle-scripts/master/android-build-app.gradle")
apply(from = "https://raw.githubusercontent.com/IVIanuu/gradle-scripts/master/android-proguard.gradle")
apply(from = "https://raw.githubusercontent.com/IVIanuu/gradle-scripts/master/java-8-android.gradle")
apply(from = "https://raw.githubusercontent.com/IVIanuu/gradle-scripts/master/kt-android-ext.gradle")
apply(from = "https://raw.githubusercontent.com/IVIanuu/gradle-scripts/master/kt-compiler-args.gradle")
//apply(from = "https://raw.githubusercontent.com/IVIanuu/gradle-scripts/master/kt-kapt.gradle")
apply(from = "https://raw.githubusercontent.com/IVIanuu/gradle-scripts/master/kt-source-sets-android.gradle")

dependencies {
    implementation(project(":essentials"))
    implementation(project(":essentials-about"))
    implementation(project(":essentials-apps"))
    implementation(project(":essentials-apps-coil"))
    implementation(project(":essentials-apps-ui"))
    implementation(project(":essentials-gestures"))
    implementation(project(":essentials-hidenavbar"))
    implementation(project(":essentials-shell"))
    implementation(project(":essentials-securesettings"))
    implementation(project(":essentials-shell"))
    implementation(project(":essentials-work"))
    //kapt(project(":essentials-compiler"))
    testImplementation(project(":essentials-test"))
}