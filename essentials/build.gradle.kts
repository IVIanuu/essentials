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

        //javaCompileOptions {
        //    annotationProcessorOptions {
        //        arguments = ["dagger.android.experimentalUseStringKeys": "true"]
        //    }
        //}
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
    api(Deps.androidxPalette)

    api(Deps.androidKtxAppCompat)
    api(Deps.androidKtxCore)
    api(Deps.androidKtxFragment)
    api(Deps.androidKtxLifecycle)
    api(Deps.androidKtxRecyclerView)
    api(Deps.androidKtxViewPager)

    api(Deps.assistedInject)

    api(Deps.contributor)
    api(Deps.contributorView)

    api(Deps.coroutinesAndroid)
    api(Deps.coroutinesCore)
    api(Deps.coroutinesRxJava)

    api(Deps.archLifecycleExtensions)
    api(Deps.archWorkRuntime)

    api(Deps.compass)
    api(Deps.compassAndroid)
    api(Deps.compassFragment)

    api(Deps.constraintLayout)

    api(Deps.dagger)
    api(Deps.daggerAndroid)
    api(Deps.daggerAndroidSupport)

    api(Deps.epoxy)
    api(Deps.epoxyKtx)
    api(Deps.epoxyPrefs)

    kapt(project(":essentials-compiler"))

    api(Deps.glide)

    api(Deps.kotlinStdLib)

    api(Deps.kPrefs)
    api(Deps.kPrefsCoroutines)
    api(Deps.kPrefsLifecycle)
    api(Deps.kPrefsRx)

    api(Deps.kSettings)
    api(Deps.kSettingsCoroutines)
    api(Deps.kSettingsLifecycle)
    api(Deps.kSettingsRx)

    api(project(":ktuples"))

    api(Deps.legacyAnnotations)

    api(Deps.liveEvent)

    api(Deps.materialComponents)
    api(Deps.materialComponentsKtx)

    api(Deps.materialDialogsCore)
    api(Deps.materialDialogsCommons)

    api(Deps.rxAndroid)
    api(Deps.rxAndroid2)
    api(Deps.rxJava)
    api(Deps.rxJavaKtx)
    api(Deps.rxKotlin)

    api(Deps.scopes)
    api(Deps.scopesAndroid)
    api(Deps.scopesArchLifecycle)
    api(Deps.scopesArchLifecycleFragment)
    api(Deps.scopesCommon)
    api(Deps.scopesCoroutines)
    api(Deps.scopesRx)

    api(Deps.superUser)

    api(Deps.stickyHeaders)

    api(Deps.timber)
    api(Deps.timberKtx)

    api(Deps.toasty)

    api(Deps.traveler)
    api(Deps.travelerAndroid)
    api(Deps.travelerFragment)
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