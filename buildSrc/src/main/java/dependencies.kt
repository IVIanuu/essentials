@file:Suppress("ClassName", "unused")

object Versions {
    // android
    const val compileSdk = 28
    const val minSdk = 21
    const val targetSdk = 28
    const val versionCode = 1
    const val versionName = "1.0"

    const val androidGradlePlugin = "3.3.0-alpha10"
    const val androidx = "1.0.0-rc02"
    const val androidxArch = "2.0.0-rc01"
    const val archWork = "1.0.0-alpha08"
    const val compass = "cc2c167fbf"
    const val constraintLayout = "1.1.3"
    const val coroutines = "0.26.0-eap13"
    const val dagger = "2.16"
    const val epoxy = "2.16.4"
    const val epoxyPrefs = "c199cb7277"
    const val fabric = "2.7.1"
    const val glide = "4.8.0"
    const val kotlin = "1.3-M2"
    const val legacySupport = "28.0.0-rc02"
    const val mavenGradle = "2.1"
    const val materialComponents = "1.0.0-rc01"
    const val materialDialogs = "0.9.6.0"
    const val r2 = "06e702d39f"
    const val rxAndroid = "2.1.0"
    const val rxAndroid2 = "6a27d8ca6d"
    const val rxJava = "2.2.1"
    const val rxKotlin = "2.3.0"
    const val rxPreferences = "2.0.0"
    const val rxSystemSettings = "aec2722332"
    const val superUser = "1.0.0.+"
    const val stickyHeaders = "578be41578"
    const val timber = "4.7.1"
    const val toasty = "1.3.0"
    const val traveler = "a46a5e6204"
}


object Deps {
    const val androidGradlePlugin = "com.android.tools.build:gradle:${Versions.androidGradlePlugin}"

    const val androidxAppCompat = "androidx.appcompat:appcompat:${Versions.androidx}"
    const val androidxCardView = "androidx.cardview:cardview:${Versions.androidx}"
    const val androidxCoreKtx = "androidx.core:core-ktx:${Versions.androidx}"
    const val androidxPalette = "androidx.palette:palette:${Versions.androidx}"

    const val archLifecycleExtensions =
        "androidx.lifecycle:lifecycle-extensions:${Versions.androidxArch}"
    const val archLifecycleCompiler =
        "androidx.lifecycle:lifecycle-compiler:${Versions.androidxArch}"

    const val archWorkRuntime = "android.arch.work:work-runtime-ktx:${Versions.archWork}"

    const val compass = "com.github.IVIanuu.compass:compass:${Versions.compass}"
    const val compassCompiler = "com.github.IVIanuu.compass:compass-compiler:${Versions.compass}"

    const val constraintLayout =
        "androidx.constraintlayout:constraintlayout:${Versions.constraintLayout}"

    const val coroutinesAndroid =
        "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.coroutines}"
    const val coroutinesCore =
        "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.coroutines}"
    const val coroutinesRxJava =
        "org.jetbrains.kotlinx:kotlinx-coroutines-rx2:${Versions.coroutines}"

    const val dagger = "com.google.dagger:dagger:${Versions.dagger}"
    const val daggerAndroid = "com.google.dagger:dagger-android:${Versions.dagger}"
    const val daggerAndroidSupport = "com.google.dagger:dagger-android-support:${Versions.dagger}"
    const val daggerCompiler = "com.google.dagger:dagger-compiler:${Versions.dagger}"
    const val daggerAndroidProcessor = "com.google.dagger:dagger-android-processor:${Versions.dagger}"

    const val epoxy = "com.airbnb.android:epoxy:${Versions.epoxy}"
    const val epoxyProcessor = "com.airbnb.android:epoxy-processor:${Versions.epoxy}"
    const val epoxyPrefs = "com.github.IVIanuu:epoxy-prefs:${Versions.epoxyPrefs}"

    const val fabric = "com.crashlytics.sdk.android:crashlytics:${Versions.fabric}@aar"

    const val glide = "com.github.bumptech.glide:glide:${Versions.glide}"

    const val kotlinGradlePlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin}"
    const val kotlinReflect = "org.jetbrains.kotlin:kotlin-reflect:${Versions.kotlin}"
    const val kotlinStdLib = "org.jetbrains.kotlin:kotlin-stdlib-jdk7:${Versions.kotlin}"

    const val legacyAnnotations =
        "com.android.support:support-annotations:${Versions.legacySupport}"

    const val materialComponents =
        "com.google.android.material:material:${Versions.materialComponents}"

    const val materialDialogsCore = "com.afollestad.material-dialogs:core:${Versions.materialDialogs}"
    const val materialDialogsCommons = "com.afollestad.material-dialogs:commons:${Versions.materialDialogs}"

    const val mavenGradlePlugin = "com.github.dcendents:android-maven-gradle-plugin:${Versions.mavenGradle}"

    const val r2GradlePlugin = "com.github.IVIanuu:r2:${Versions.r2}"

    const val rxAndroid = "io.reactivex.rxjava2:rxandroid:${Versions.rxAndroid}"
    const val rxAndroid2 = "com.github.IVIanuu:rx-android:${Versions.rxAndroid2}"

    const val rxJava = "io.reactivex.rxjava2:rxjava:${Versions.rxJava}"
    const val rxKotlin = "io.reactivex.rxjava2:rxkotlin:${Versions.rxKotlin}"
    const val rxPreferences = "com.f2prateek.rx.preferences2:rx-preferences:${Versions.rxPreferences}"
    const val rxSystemSettings =
        "com.github.IVIanuu:rx-system-settings:${Versions.rxSystemSettings}"

    const val superUser = "eu.chainfire:libsuperuser:${Versions.superUser}"

    const val stickyHeaders = "com.github.IVIanuu:sticky-headers:${Versions.stickyHeaders}"

    const val timber = "com.jakewharton.timber:timber:${Versions.timber}"

    const val toasty = "com.github.GrenderG:Toasty:${Versions.toasty}"

    const val traveler = "com.github.IVIanuu:traveler:${Versions.traveler}"
}