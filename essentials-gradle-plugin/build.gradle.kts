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
    id("java-gradle-plugin")
    kotlin("jvm")
    kotlin("kapt")
    id("de.fuerstenau.buildconfig")
}

apply(from = "https://raw.githubusercontent.com/IVIanuu/gradle-scripts/master/java-8.gradle")
apply(from = "https://raw.githubusercontent.com/IVIanuu/gradle-scripts/master/kt-compiler-args.gradle")

gradlePlugin {
    plugins {
        create("essentialsPlugin") {
            id = "com.ivianuu.essentials"
            implementationClass =
                "com.ivianuu.essentials.gradle.EssentialsPlugin"
        }
        create("composePlugin") {
            id = "com.ivianuu.essentials.compose"
            implementationClass =
                "com.ivianuu.essentials.gradle.ComposePlugin"
        }
    }
}

buildConfig {
    clsName = "BuildConfig"
    packageName = "com.ivianuu.essentials.gradle"

    version = property("VERSION_NAME").toString()
    buildConfigField("String", "GROUP_ID", property("GROUP").toString())
    buildConfigField("String", "ARTIFACT_ID", property("POM_ARTIFACT_ID").toString())

    buildConfigField("String", "COMPOSE_GROUP_ID", "androidx.compose.compiler")
    buildConfigField("String", "COMPOSE_ARTIFACT_ID", "compiler")
    buildConfigField("String", "COMPOSE_VERSION", Deps.AndroidX.Compose.version)
}

dependencies {
    implementation(Deps.autoService)
    kapt(Deps.autoService)

    implementation(Deps.Kotlin.gradlePlugin)
    implementation(Deps.Kotlin.gradlePluginApi)

    api(Deps.androidGradlePlugin)
    api(Deps.KotlinSerialization.gradlePlugin)
    api(Deps.Injekt.gradlePlugin)
}

plugins.apply("com.vanniktech.maven.publish")
