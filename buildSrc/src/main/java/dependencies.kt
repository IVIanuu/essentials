@file:Suppress("ClassName", "unused")

object Versions {
    // android
    const val compileSdk = 28
    const val minSdk = 21
    const val targetSdk = 28
    const val versionCode = 1
    const val versionName = "1.0"

    const val androidGradlePlugin = "3.1.3"
    const val archLifecycle = "1.1.1"
    const val autoDispose = "0.8.0"
    const val butterknife = "9.0.0-SNAPSHOT"
    const val constraintLayout = "1.1.0"
    const val dagger = "2.16"
    const val daggerExtensions = "8ffa8b656b"
    const val epoxy = "2.12.0"
    const val fabric = "2.7.1"
    const val kotlin = "1.2.41"
    const val ktx = "0.3"
    const val mavenGradle = "2.1"
    const val materialDialogs = "0.9.6.0"
    const val navi = "c521dab5e1"
    const val rxActivityResult = "b16b2d4cf9"
    const val rxAndroid = "2.0.2"
    const val rxBinding = "2.1.1"
    const val rxBroadcastReceiver = "13d01a5167"
    const val rxJava = "2.1.13"
    const val rxKotlin = "2.2.0"
    const val rxPermissions = "aa7de40f71"
    const val rxPreferences = "2.0.0"
    const val rxPopupMenu = "cc0f585ec0"
    const val seekBarPreference = "e4f09c6636"
    const val support = "28.0.0-alpha3"
    const val timber = "4.7.0"
    const val traveler = "f878fa984b"
}

object Deps {
    const val androidGradlePlugin = "com.android.tools.build:gradle:${Versions.androidGradlePlugin}"

    const val archLifecycleExtensions = "android.arch.lifecycle:extensions:${Versions.archLifecycle}"
    const val archLifecycleCompiler = "android.arch.lifecycle:compiler:${Versions.archLifecycle}"

    const val autoDispose = "com.uber.autodispose:autodispose-kotlin:${Versions.autoDispose}"
    const val autoDisposeAndroid = "com.uber.autodispose:autodispose-android-kotlin:${Versions.autoDispose}"
    const val autoDisposeArch = "com.uber.autodispose:autodispose-android-archcomponents-kotlin:${Versions.autoDispose}"

    const val butterknifeGradlePlugin = "com.jakewharton:butterknife-gradle-plugin:${Versions.butterknife}"

    const val constraintLayout = "com.android.support.constraint:constraint-layout:${Versions.constraintLayout}"

    const val dagger = "com.google.dagger:dagger:${Versions.dagger}"
    const val daggerAndroid = "com.google.dagger:dagger-android:${Versions.dagger}"
    const val daggerAndroidSupport = "com.google.dagger:dagger-android-support:${Versions.dagger}"
    const val daggerCompiler = "com.google.dagger:dagger-compiler:${Versions.dagger}"
    const val daggerAndroidProcessor = "com.google.dagger:dagger-android-processor:${Versions.dagger}"

    const val daggerExtensions = "com.github.IVIanuu.DaggerExtensions:daggerextensions:${Versions.daggerExtensions}"
    const val daggerExtensionsView = "com.github.IVIanuu.DaggerExtensions:daggerextensions-view:${Versions.daggerExtensions}"
    const val daggerExtensionsCompiler = "com.github.IVIanuu.DaggerExtensions:daggerextensions-compiler:${Versions.daggerExtensions}"

    const val epoxy = "com.airbnb.android:epoxy:${Versions.epoxy}"
    const val epoxyProcessor = "com.airbnb.android:epoxy-processor:${Versions.epoxy}"

    const val fabric = "com.crashlytics.sdk.android:crashlytics:${Versions.fabric}@aar"

    const val ktx = "androidx.core:core-ktx:${Versions.ktx}"
    const val kotlinGradlePlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin}"
    const val kotlinStdLib = "org.jetbrains.kotlin:kotlin-stdlib-jre7:${Versions.kotlin}"

    const val materialDialogsCore = "com.afollestad.material-dialogs:core:${Versions.materialDialogs}"
    const val materialDialogsCommons = "com.afollestad.material-dialogs:commons:${Versions.materialDialogs}"

    const val mavenGradlePlugin = "com.github.dcendents:android-maven-gradle-plugin:${Versions.mavenGradle}"

    const val rxActivityResult = "com.github.IVIanuu:RxActivityResult:${Versions.rxActivityResult}"
    const val rxAndroid = "io.reactivex.rxjava2:rxandroid:${Versions.rxAndroid}"

    const val rxBinding = "com.jakewharton.rxbinding2:rxbinding-kotlin:${Versions.rxBinding}"
    const val rxBindingDesign = "com.jakewharton.rxbinding2:rxbinding-design-kotlin:${Versions.rxBinding}"
    const val rxBindingRecyclerView = "com.jakewharton.rxbinding2:rxbinding-recyclerview-v7-kotlin:${Versions.rxBinding}"
    const val rxBindingSupportAppCompatV7 = "com.jakewharton.rxbinding2:rxbinding-appcompat-v7-kotlin:${Versions.rxBinding}"
    const val rxBindingSupportV4 = "com.jakewharton.rxbinding2:rxbinding-support-v4-kotlin:${Versions.rxBinding}"

    const val rxBroadcastReceiver = "com.github.IVIanuu:RxBroadcastReceiver:${Versions.rxBroadcastReceiver}"
    const val rxJava = "io.reactivex.rxjava2:rxjava:${Versions.rxJava}"
    const val rxKotlin = "io.reactivex.rxjava2:rxkotlin:${Versions.rxKotlin}"
    const val rxPermissions = "com.github.IVIanuu:RxPermissions:${Versions.rxPermissions}"
    const val rxPreferences = "com.f2prateek.rx.preferences2:rx-preferences:${Versions.rxPreferences}"

    const val rxPopupMenu = "com.github.IVIanuu:RxPopupmenu:${Versions.rxPopupMenu}"

    const val seekBarPreference = "com.github.IVIanuu:SeekBarPreference:${Versions.seekBarPreference}"

    const val supportAppCompat = "com.android.support:appcompat-v7:${Versions.support}"
    const val supportCardView = "com.android.support:cardview-v7:${Versions.support}"
    const val supportDesign = "com.android.support:design:${Versions.support}"
    const val supportPalette = "com.android.support:palette-v7:${Versions.support}"
    const val supportPreference = "com.android.support:preference-v7:${Versions.support}"

    const val timber = "com.jakewharton.timber:timber:${Versions.timber}"

    const val traveler = "com.github.IVIanuu.Traveler:traveler:${Versions.traveler}"
    const val travelerCompass = "com.github.IVIanuu.Traveler:traveler-compass:${Versions.traveler}"
    const val travelerCompassCompiler = "com.github.IVIanuu.Traveler:traveler-compass-compiler:${Versions.traveler}"
    const val travelerFragments = "com.github.IVIanuu.Traveler:traveler-fragments:${Versions.traveler}"
    const val travelerLifecycleObserver = "com.github.IVIanuu.Traveler:traveler-lifecycleobserver:${Versions.traveler}"
}