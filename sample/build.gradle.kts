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

plugins {
    id("com.android.application")
    id("com.ivianuu.essentials")
    id("androidx.compose")
    kotlin("android")
    kotlin("kapt")
}

apply(from = "https://raw.githubusercontent.com/IVIanuu/gradle-scripts/master/android-build-app.gradle")
apply(from = "https://raw.githubusercontent.com/IVIanuu/gradle-scripts/master/android-proguard.gradle")
apply(from = "https://raw.githubusercontent.com/IVIanuu/gradle-scripts/master/java-8-android.gradle")
apply(from = "https://raw.githubusercontent.com/IVIanuu/gradle-scripts/master/kt-compiler-args.gradle")
apply(from = "https://raw.githubusercontent.com/IVIanuu/gradle-scripts/master/kt-source-sets-android.gradle")

android {
    // todo remove once fixed
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

injekt {
    generateMergeComponents = true
}

dependencies {
    implementation(project(":essentials-android"))
    implementation(project(":essentials-android-core"))
    implementation(project(":essentials-about"))
    implementation(project(":essentials-accessibility"))
    implementation(project(":essentials-apps"))
    implementation(project(":essentials-apps-coil"))
    implementation(project(":essentials-apps-ui"))
    implementation(project(":essentials-backup"))
    releaseImplementation(project(":essentials-billing-release"))
    debugImplementation(project(":essentials-billing-debug"))
    implementation(project(":essentials-boot"))
    implementation(project(":essentials-foreground"))
    implementation(project(":essentials-gestures"))
    implementation(project(":essentials-hide-nav-bar"))
    implementation(project(":essentials-notification-listener"))
    implementation(project(":essentials-permission"))
    implementation(project(":essentials-process-restart"))
    implementation(project(":essentials-shell"))
    implementation(project(":essentials-shell"))
    implementation(project(":essentials-shortcut-picker"))
    implementation(project(":essentials-tile"))
    implementation(project(":essentials-torch"))
    implementation(project(":essentials-twilight"))
    implementation(project(":essentials-unlock"))
    implementation(project(":essentials-work"))
    kapt(project(":essentials-compiler"))

    debugImplementation(Deps.leakCanary)

    testImplementation(project(":essentials-android-test"))
    testImplementation(project(":essentials-test"))
}