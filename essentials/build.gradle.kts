import org.gradle.jvm.tasks.Jar
import org.jetbrains.kotlin.gradle.internal.AndroidExtensionsExtension

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
    id("kotlin-android")
    id("kotlin-android-extensions")
    id("kotlin-kapt")
    id("com.ivianuu.r2")
    id("com.github.dcendents.android-maven")
}

group = "com.github.ivianuu"

android {
    compileSdkVersion(Build.compileSdk)

    defaultConfig {
        buildToolsVersion = Build.buildToolsVersion
        minSdkVersion(Build.minSdk)
        targetSdkVersion(Build.targetSdk)
        consumerProguardFile("proguard-rules.txt")

        javaCompileOptions {
            annotationProcessorOptions {
                argument("dagger.formatGeneratedSource", "disabled")
            }
        }
    }

    androidExtensions {
        // isExperimental = true
        configure(delegateClosureOf<AndroidExtensionsExtension> {
            isExperimental = true
        })
    }

    kapt {
        correctErrorTypes = true
    }
}

dependencies {
    api(Deps.androidxAppCompat)
    api(Deps.androidxCardView)
    api(Deps.androidxCoreKtx)

    api(Deps.coroutinesAndroid)
    api(Deps.coroutinesCore)
    api(Deps.coroutinesRxJava)

    api(Deps.androidxLifecycleExtensions)

    api(Deps.constraintLayout)

    api(Deps.director)
    api(Deps.directorArchLifecycle)
    api(Deps.directorCommon)
    api(Deps.directorDialog)
    api(Deps.directorScopes)
    api(Deps.directorTraveler)
    api(Deps.directorViewPager)

    api(Deps.epoxy)
    api(Deps.epoxyKtx)
    api(Deps.epoxyPrefs)

    kapt(project(":essentials-compiler"))

    api(Deps.injekt)
    api(Deps.injektAndroid)
    api(Deps.injektReflect)

    api(Deps.kommonAppCompat)
    api(Deps.kommonCore)
    api(Deps.kommonLifecycle)
    api(Deps.kommonMaterial)
    api(Deps.kommonRecyclerView)
    api(Deps.kommonViewPager)

    api(Deps.kotlinStdLib)

    api(Deps.kPrefs)
    api(Deps.kPrefsCoroutines)
    api(Deps.kPrefsLiveData)
    api(Deps.kPrefsRx)

    api(Deps.kSettings)
    api(Deps.kSettingsCoroutines)
    api(Deps.kSettingsLiveData)
    api(Deps.kSettingsRx)

    api(project(":ktuples"))

    api(Deps.legacyAnnotations)

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
    api(Deps.scopesLiveData)
    api(Deps.scopesCommon)
    api(Deps.scopesCoroutines)
    api(Deps.scopesRx)

    api(Deps.stateStore)
    api(Deps.stateStoreAndroid)
    api(Deps.stateStoreCoroutines)
    api(Deps.stateStoreLiveData)
    api(Deps.stateStoreRx)

    api(Deps.timber)
    api(Deps.timberKtx)

    api(Deps.traveler)
    api(Deps.travelerAndroid)
    api(Deps.travelerLifecycle)
}

val sourcesJar = task("sourcesJar", Jar::class) {
    from(android.sourceSets["main"].java.srcDirs)
    classifier = "sources"
}

val javadoc = task("javadoc", Javadoc::class) {
    isFailOnError = false
    source = android.sourceSets["main"].java.sourceFiles
    classpath += project.files(android.bootClasspath.joinToString(File.pathSeparator))
    classpath += configurations.compile
}

val javadocJar = task("javadocJar", Jar::class) {
    dependsOn(javadoc)
    classifier = "javadoc"
    from(javadoc.destinationDir)
}

artifacts {
    add("archives", sourcesJar)
    add("archives", javadocJar)
}