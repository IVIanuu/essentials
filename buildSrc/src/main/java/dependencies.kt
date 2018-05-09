@file:Suppress("ClassName", "unused")

object Versions {
    // android
    const val compileSdk = 27
    const val minSdk = 21
    const val targetSdk = 27
    const val versionCode = 1
    const val versionName = "1.0"

    const val androidGradlePlugin = "3.1.2"
    const val archComponents = "1.1.1"
    const val autoDispose = "6f10c435c1"
    const val butterknife = "9.0.0-SNAPSHOT"
    const val constraintLayout = "1.1.0"
    const val dagger = "2.16"
    const val epoxy = "2.12.0"
    const val fabric = "2.7.1"
    const val kotlin = "1.2.41"
    const val ktx = "0.3"
    const val liveDataExtensions = "1.0.1"
    const val mavenGradle = "2.1"
    const val materialDialogs = "0.9.6.0"
    const val rxActivityResult = "6da49c1fcf"
    const val rxAndroid = "2.0.2"
    const val rxJava = "2.1.13"
    const val rxKotlin = "2.2.0"
    const val rxPermissions = "981f695508"
    const val rxPreferences = "2.0.0"
    const val seekBarPreference = "7bd4049da0"
    const val support = "27.1.1"
    const val timber = "4.7.0"
    const val traveler = "bc51a339ad"
}
//
object Deps {
    const val androidGradlePlugin = "com.android.tools.build:gradle:${Versions.androidGradlePlugin}"

    const val archComponentsLifecycleExtensions = "android.arch.lifecycle:extensions:${Versions.archComponents}"

    const val autoDispose = "com.github.IVIanuu.AutoDispose:autodispose:${Versions.autoDispose}"
    const val autoDisposeView = "com.github.IVIanuu.AutoDispose:autodispose-view:${Versions.autoDispose}"

    const val butterknifeGradlePlugin = "com.jakewharton:butterknife-gradle-plugin:${Versions.butterknife}"

    const val constraintLayout = "com.android.support.constraint:constraint-layout:${Versions.constraintLayout}"

    const val dagger = "com.google.dagger:dagger:${Versions.dagger}"
    const val daggerAndroid = "com.google.dagger:dagger-android:${Versions.dagger}"
    const val daggerAndroidSupport = "com.google.dagger:dagger-android-support:${Versions.dagger}"
    const val daggerCompiler = "com.google.dagger:dagger-compiler:${Versions.dagger}"
    const val daggerAndroidProcessor = "com.google.dagger:dagger-android-processor:${Versions.dagger}"

    const val epoxy = "com.airbnb.android:epoxy:${Versions.epoxy}"
    const val epoxyProcessor = "com.airbnb.android:epoxy-processor:${Versions.epoxy}"

    const val fabric = "com.crashlytics.sdk.android:crashlytics:${Versions.fabric}@aar"

    const val ktx = "androidx.core:core-ktx:${Versions.ktx}"
    const val kotlinGradlePlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin}"
    const val kotlinStdLib = "org.jetbrains.kotlin:kotlin-stdlib-jre7:${Versions.kotlin}"

    const val liveDataExtensions = "com.snakydesign.livedataextensions:lives:${Versions.liveDataExtensions}"

    const val materialDialogsCore = "com.afollestad.material-dialogs:core:${Versions.materialDialogs}"
    const val materialDialogsCommons = "com.afollestad.material-dialogs:commons:${Versions.materialDialogs}"

    const val mavenGradlePlugin = "com.github.dcendents:android-maven-gradle-plugin:${Versions.mavenGradle}"

    const val rxActivityResult = "com.github.IVIanuu:RxActivityResult:${Versions.rxActivityResult}"
    const val rxAndroid = "io.reactivex.rxjava2:rxandroid:${Versions.rxAndroid}"
    const val rxJava = "io.reactivex.rxjava2:rxjava:${Versions.rxJava}"
    const val rxKotlin = "io.reactivex.rxjava2:rxkotlin:${Versions.rxKotlin}"
    const val rxPermissions = "com.github.IVIanuu:RxPermissions:${Versions.rxPermissions}"
    const val rxPreferences = "com.f2prateek.rx.preferences2:rx-preferences:${Versions.rxPreferences}"

    const val seekBarPreference = "com.github.IVIanuu:SeekBarPreference:${Versions.seekBarPreference}"

    const val supportAppCompat = "com.android.support:appcompat-v7:${Versions.support}"
    const val supportCardView = "com.android.support:cardview-v7:${Versions.support}"
    const val supportDesign = "com.android.support:design:${Versions.support}"
    const val supportPalette = "com.android.support:palette-v7:${Versions.support}"
    const val supportPreference = "com.android.support:preference-v7:${Versions.support}"

    const val timber = "com.jakewharton.timber:timber:${Versions.timber}"

    const val traveler = "com.github.IVIanuu.Traveler:traveler:${Versions.traveler}"
    const val travelerAndroid = "com.github.IVIanuu.Traveler:traveler-android:${Versions.traveler}"
    const val travelerKeys = "com.github.IVIanuu.Traveler:traveler-keys:${Versions.traveler}"
}