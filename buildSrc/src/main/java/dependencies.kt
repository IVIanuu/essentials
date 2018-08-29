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
    const val archWork = "1.0.0-alpha08"
    const val autoDispose = "1.0.0-RC2"
    const val butterknife = "9.0.0-SNAPSHOT"
    const val compass = "986d5cec7f"
    const val constraintLayout = "1.1.2"
    const val dagger = "2.17"
    const val epoxy = "2.16.2"
    const val epoxyPrefs = "43d96b79e9"
    const val fabric = "2.7.1"
    const val fastScroll = "1.0.17"
    const val glide = "4.7.1"
    const val kotlin = "1.2.61"
    const val ktx = "0.3"
    const val mavenGradle = "2.1"
    const val materialDialogs = "0.9.6.0"
    const val rxAndroid = "2.1.0"
    const val rxAndroid2 = "6a27d8ca6d"
    const val rxBinding = "2.1.1"
    const val rxJava = "2.1.16"
    const val rxKotlin = "2.2.0"
    const val rxPreferences = "2.0.0"
    const val rxSystemSettings = "aec2722332"
    const val superUser = "1.0.0.+"
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

    const val autoDispose = "com.uber.autodispose:autodispose:${Versions.autoDispose}"
    const val autoDisposeKtx = "com.uber.autodispose:autodispose-ktx:${Versions.autoDispose}"
    const val autoDisposeLifecycle =
        "com.uber.autodispose:autodispose-lifecycle:${Versions.autoDispose}"
    const val autoDisposeLifecycleKtx =
        "com.uber.autodispose:autodispose-lifecycle-ktx:${Versions.autoDispose}"
    const val autoDisposeAndroid =
        "com.uber.autodispose:autodispose-android:${Versions.autoDispose}"
    const val autoDisposeAndroidKtx =
        "com.uber.autodispose:autodispose-android-ktx:${Versions.autoDispose}"
    const val autoDisposeArch =
        "com.uber.autodispose:autodispose-android-archcomponents:${Versions.autoDispose}"
    const val autoDisposeArchKtx =
        "com.uber.autodispose:autodispose-android-archcomponents-ktx:${Versions.autoDispose}"

    const val butterknifeGradlePlugin = "com.jakewharton:butterknife-gradle-plugin:${Versions.butterknife}"

    const val compass = "com.github.IVIanuu.compass:compass:${Versions.compass}"
    const val compassCompiler = "com.github.IVIanuu.compass:compass-compiler:${Versions.compass}"

    const val constraintLayout = "com.android.support.constraint:constraint-layout:${Versions.constraintLayout}"

    const val dagger = "com.google.dagger:dagger:${Versions.dagger}"
    const val daggerAndroid = "com.google.dagger:dagger-android:${Versions.dagger}"
    const val daggerAndroidSupport = "com.google.dagger:dagger-android-support:${Versions.dagger}"
    const val daggerCompiler = "com.google.dagger:dagger-compiler:${Versions.dagger}"
    const val daggerAndroidProcessor = "com.google.dagger:dagger-android-processor:${Versions.dagger}"

    const val epoxy = "com.airbnb.android:epoxy:${Versions.epoxy}"
    const val epoxyProcessor = "com.airbnb.android:epoxy-processor:${Versions.epoxy}"
    const val epoxyPrefs = "com.github.IVIanuu:epoxy-prefs:${Versions.epoxyPrefs}"

    const val fabric = "com.crashlytics.sdk.android:crashlytics:${Versions.fabric}@aar"

    const val fastScroll = "com.simplecityapps:recyclerview-fastscroll:${Versions.fastScroll}"

    const val glide = "com.github.bumptech.glide:glide:${Versions.glide}"

    const val ktx = "androidx.core:core-ktx:${Versions.ktx}"
    const val kotlinGradlePlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin}"
    const val kotlinStdLib = "org.jetbrains.kotlin:kotlin-stdlib-jre7:${Versions.kotlin}"

    const val materialDialogsCore = "com.afollestad.material-dialogs:core:${Versions.materialDialogs}"
    const val materialDialogsCommons = "com.afollestad.material-dialogs:commons:${Versions.materialDialogs}"

    const val mavenGradlePlugin = "com.github.dcendents:android-maven-gradle-plugin:${Versions.mavenGradle}"

    const val rxAndroid = "io.reactivex.rxjava2:rxandroid:${Versions.rxAndroid}"
    const val rxAndroid2 = "com.github.IVIanuu:rx-android:${Versions.rxAndroid2}"

    const val rxBinding = "com.jakewharton.rxbinding2:rxbinding-kotlin:${Versions.rxBinding}"
    const val rxBindingDesign = "com.jakewharton.rxbinding2:rxbinding-design-kotlin:${Versions.rxBinding}"
    const val rxBindingRecyclerView = "com.jakewharton.rxbinding2:rxbinding-recyclerview-v7-kotlin:${Versions.rxBinding}"
    const val rxBindingSupportAppCompatV7 = "com.jakewharton.rxbinding2:rxbinding-appcompat-v7-kotlin:${Versions.rxBinding}"
    const val rxBindingSupportV4 = "com.jakewharton.rxbinding2:rxbinding-support-v4-kotlin:${Versions.rxBinding}"

    const val rxJava = "io.reactivex.rxjava2:rxjava:${Versions.rxJava}"
    const val rxKotlin = "io.reactivex.rxjava2:rxkotlin:${Versions.rxKotlin}"
    const val rxPreferences = "com.f2prateek.rx.preferences2:rx-preferences:${Versions.rxPreferences}"
    const val rxSystemSettings =
        "com.github.IVIanuu:rx-system-settings:${Versions.rxSystemSettings}"

    const val superUser = "eu.chainfire:libsuperuser:${Versions.superUser}"

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