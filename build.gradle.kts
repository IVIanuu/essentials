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

buildscript {
    repositories {
        mavenLocal()
        maven("https://dl.bintray.com/ivianuu/maven/")
        google()
        jcenter()
        mavenCentral()
        maven("https://jitpack.io")
        maven("https://oss.sonatype.org/content/repositories/snapshots")
        maven("https://dl.bintray.com/kotlin/kotlin-eap")
        maven(Deps.AndroidX.Compose.snapshotUrl)
        maven("https://plugins.gradle.org/m2")
        maven("https://maven.fabric.io/public")
        maven("https://kotlin.bintray.com/kotlinx")
    }

    dependencies {
        classpath(Deps.androidGradlePlugin)
        classpath(Deps.buildConfigGradlePlugin)
        classpath(Deps.bintrayGradlePlugin)
        classpath(Deps.dexcountGradlePlugin)
        classpath(Deps.essentialsGradlePlugin)
        classpath(Deps.Kotlin.gradlePlugin)
        classpath(Deps.mavenGradlePlugin)
    }
}

allprojects {
    // todo move
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions {
            useIR = true
        }
    }

    // todo remove
    configurations.all {
        resolutionStrategy.force("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.3.9-SNAPSHOT")
        resolutionStrategy.force("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.9-SNAPSHOT")
        resolutionStrategy.force("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.3.9-SNAPSHOT")
    }

    repositories {
        mavenLocal()
        maven("https://dl.bintray.com/ivianuu/maven/")
        google()
        jcenter()
        mavenCentral()
        maven("https://jitpack.io")
        maven("https://oss.sonatype.org/content/repositories/snapshots")
        maven(Deps.AndroidX.Compose.snapshotUrl)
        maven("https://dl.bintray.com/kotlin/kotlin-eap")
        maven("https://maven.fabric.io/public")
        maven("https://kotlin.bintray.com/kotlinx")
    }
}