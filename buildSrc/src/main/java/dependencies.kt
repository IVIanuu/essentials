@file:Suppress("ClassName", "unused")

object Versions {
    // android
    const val compileSdk = 28
    const val minSdk = 21
    const val targetSdk = 28
    const val versionCode = 1
    const val versionName = "1.0"

    const val androidGradlePlugin = "3.1.4"
    const val archLifecycle = "1.1.1"
    const val archWork = "1.0.0-alpha06"
    const val autoDispose = "0d27692156"
    const val butterknife = "9.0.0-SNAPSHOT"
    const val compass = "738292d98d"
    const val constraintLayout = "1.1.2"
    const val dagger = "2.16"
    const val daggerExtensions = "4b3be761d2"
    const val epoxy = "2.14.0"
    const val epoxyPrefs = "16ab985ec6"
    const val fabric = "2.7.1"
    const val fastScroll = "1.0.17"
    const val kotlin = "1.2.51"
    const val ktx = "0.3"
    const val mavenGradle = "2.1"
    const val materialDialogs = "0.9.6.0"
    const val rxActivityResult = "7aa338925c"
    const val rxAndroid = "2.0.2"
    const val rxBinding = "2.1.1"
    const val rxBroadcastReceiver = "13d01a5167"
    const val rxContentObserver = "ffec76b019"
    const val rxJava = "2.1.16"
    const val rxKotlin = "2.2.0"
    const val rxPermissions = "de32df8112"
    const val rxPreferences = "2.0.0"
    const val rxPopupMenu = "fc03116f7e"
    const val rxServiceConnection = "0130e7905a"
    const val rxSystemSettings = "aec2722332"
    const val support = "28.0.0-alpha3"
    const val stickyHeaders = "0f48d56a25"
    const val timber = "4.7.0"
    const val toasty = "1.3.0"
    const val traveler = "1eca65e728"
}

object Deps {
    const val androidGradlePlugin = "com.android.tools.build:gradle:${Versions.androidGradlePlugin}"

    const val archLifecycleExtensions = "android.arch.lifecycle:extensions:${Versions.archLifecycle}"
    const val archLifecycleCompiler = "android.arch.lifecycle:compiler:${Versions.archLifecycle}"

    const val archWorkRuntime = "android.arch.work:work-runtime-ktx:${Versions.archWork}"

    const val autoDispose = "com.github.IVIanuu.auto-dispose:autodispose:${Versions.autoDispose}"
    const val autoDisposeAndroid =
        "com.github.IVIanuu.auto-dispose:autodispose-android:${Versions.autoDispose}"
    const val autoDisposeArch =
        "com.github.IVIanuu.auto-dispose:autodispose-arch:${Versions.autoDispose}"

    const val butterknifeGradlePlugin = "com.jakewharton:butterknife-gradle-plugin:${Versions.butterknife}"

    const val compass = "com.github.IVIanuu.compass:compass:${Versions.compass}"
    const val compassCompiler = "com.github.IVIanuu.compass:compass-compiler:${Versions.compass}"

    const val constraintLayout = "com.android.support.constraint:constraint-layout:${Versions.constraintLayout}"

    const val dagger = "com.google.dagger:dagger:${Versions.dagger}"
    const val daggerAndroid = "com.google.dagger:dagger-android:${Versions.dagger}"
    const val daggerAndroidSupport = "com.google.dagger:dagger-android-support:${Versions.dagger}"
    const val daggerCompiler = "com.google.dagger:dagger-compiler:${Versions.dagger}"
    const val daggerAndroidProcessor = "com.google.dagger:dagger-android-processor:${Versions.dagger}"

    const val daggerExtensions = "com.github.IVIanuu.dagger-extensions:daggerextensions:${Versions.daggerExtensions}"
    const val daggerExtensionsCompiler =
        "com.github.IVIanuu.dagger-extensions:daggerextensions-compiler:${Versions.daggerExtensions}"
    const val daggerExtensionsView = "com.github.IVIanuu.dagger-extensions:daggerextensions-view:${Versions.daggerExtensions}"
    const val daggerExtensionsWork =
        "com.github.IVIanuu.dagger-extensions:daggerextensions-work:${Versions.daggerExtensions}"

    const val epoxy = "com.airbnb.android:epoxy:${Versions.epoxy}"
    const val epoxyProcessor = "com.airbnb.android:epoxy-processor:${Versions.epoxy}"
    const val epoxyPrefs = "com.github.IVIanuu:epoxy-prefs:${Versions.epoxyPrefs}"

    const val fabric = "com.crashlytics.sdk.android:crashlytics:${Versions.fabric}@aar"

    const val fastScroll = "com.simplecityapps:recyclerview-fastscroll:${Versions.fastScroll}"

    const val ktx = "androidx.core:core-ktx:${Versions.ktx}"
    const val kotlinGradlePlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin}"
    const val kotlinStdLib = "org.jetbrains.kotlin:kotlin-stdlib-jre7:${Versions.kotlin}"

    const val materialDialogsCore = "com.afollestad.material-dialogs:core:${Versions.materialDialogs}"
    const val materialDialogsCommons = "com.afollestad.material-dialogs:commons:${Versions.materialDialogs}"

    const val mavenGradlePlugin = "com.github.dcendents:android-maven-gradle-plugin:${Versions.mavenGradle}"

    const val rxActivityResult = "com.github.IVIanuu:rx-activity-result:${Versions.rxActivityResult}"
    const val rxAndroid = "io.reactivex.rxjava2:rxandroid:${Versions.rxAndroid}"

    const val rxBinding = "com.jakewharton.rxbinding2:rxbinding-kotlin:${Versions.rxBinding}"
    const val rxBindingDesign = "com.jakewharton.rxbinding2:rxbinding-design-kotlin:${Versions.rxBinding}"
    const val rxBindingRecyclerView = "com.jakewharton.rxbinding2:rxbinding-recyclerview-v7-kotlin:${Versions.rxBinding}"
    const val rxBindingSupportAppCompatV7 = "com.jakewharton.rxbinding2:rxbinding-appcompat-v7-kotlin:${Versions.rxBinding}"
    const val rxBindingSupportV4 = "com.jakewharton.rxbinding2:rxbinding-support-v4-kotlin:${Versions.rxBinding}"

    const val rxBroadcastReceiver = "com.github.IVIanuu:rx-broadcast-receiver:${Versions.rxBroadcastReceiver}"
    const val rxContentObserver =
        "com.github.IVIanuu:rx-content-observer:${Versions.rxContentObserver}"
    const val rxJava = "io.reactivex.rxjava2:rxjava:${Versions.rxJava}"
    const val rxKotlin = "io.reactivex.rxjava2:rxkotlin:${Versions.rxKotlin}"
    const val rxPermissions = "com.github.IVIanuu:rx-permissions:${Versions.rxPermissions}"
    const val rxPreferences = "com.f2prateek.rx.preferences2:rx-preferences:${Versions.rxPreferences}"
    const val rxPopupMenu = "com.github.IVIanuu:rx-popup-menu:${Versions.rxPopupMenu}"
    const val rxServiceConnection =
        "com.github.IVIanuu:rx-service-connection:${Versions.rxServiceConnection}"
    const val rxSystemSettings =
        "com.github.IVIanuu:rx-system-settings:${Versions.rxSystemSettings}"

    const val supportAppCompat = "com.android.support:appcompat-v7:${Versions.support}"
    const val supportCardView = "com.android.support:cardview-v7:${Versions.support}"
    const val supportDesign = "com.android.support:design:${Versions.support}"
    const val supportPalette = "com.android.support:palette-v7:${Versions.support}"

    const val stickyHeaders = "com.github.IVIanuu:sticky-headers:${Versions.stickyHeaders}"

    const val timber = "com.jakewharton.timber:timber:${Versions.timber}"

    const val toasty = "com.github.GrenderG:Toasty:${Versions.toasty}"

    const val traveler = "com.github.IVIanuu.traveler:traveler:${Versions.traveler}"
    const val travelerLifecycleObserver = "com.github.IVIanuu.traveler:traveler-lifecycleobserver:${Versions.traveler}"
}