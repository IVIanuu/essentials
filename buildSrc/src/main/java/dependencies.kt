@file:Suppress("ClassName", "unused")

object Versions {
    // android
    const val compileSdk = 28
    const val minSdk = 21
    const val targetSdk = 28
    const val versionCode = 1
    const val versionName = "SNAPSHOT"
    const val groupId = "com.ivianuu.essentials"

    const val androidGradlePlugin = "3.2.0"
    const val androidx = "1.0.0"
    const val androidxArch = "2.0.0-rc01"
    const val androidKtx = "SNAPSHOT"
    const val archWork = "1.0.0-alpha09"
    const val assistedInject = "SNAPSHOT"
    const val compass = "SNAPSHOT"
    const val constraintLayout = "1.1.3"
    const val contributor = "SNAPSHOT"
    const val coroutines = "0.26.1-eap13"
    const val dagger = "2.17"
    const val director = "SNAPSHOT"
    const val epoxy = "2.18.0"
    const val epoxyKtx = "SNAPSHOT"
    const val epoxyPrefs = "SNAPSHOT"
    const val fabric = "2.7.1"
    const val glide = "4.8.0"
    const val kotlin = "1.3.0-rc-57"
    const val kPrefs = "SNAPSHOT"
    const val kSystemSettings = "SNAPSHOT"
    const val legacySupport = "28.0.0"
    const val liveEvent = "SNAPSHOT"
    const val mavenGradle = "2.1"
    const val materialComponents = "1.0.0"
    const val materialComponentsKtx = "SNAPSHOT"
    const val materialDialogs = "0.9.6.0"
    const val r2 = "06e702d39f"
    const val rxAndroid = "2.1.0"
    const val rxAndroid2 = "SNAPSHOT"
    const val rxJava = "2.2.2"
    const val rxJavaKtx = "SNAPSHOT"
    const val rxKotlin = "2.3.0"
    const val rxLifecycle = "52409cf2db"
    const val superUser = "1.0.0.+"
    const val stickyHeaders = "SNAPSHOT"
    const val timber = "4.7.1"
    const val timberKtx = "SNAPSHOT"
    const val toasty = "1.3.0"
    const val traveler = "SNAPSHOT"
}

object Deps {
    const val androidGradlePlugin = "com.android.tools.build:gradle:${Versions.androidGradlePlugin}"

    const val androidxAppCompat = "androidx.appcompat:appcompat:${Versions.androidx}"
    const val androidxCardView = "androidx.cardview:cardview:${Versions.androidx}"
    const val androidxCoreKtx = "androidx.core:core-ktx:${Versions.androidx}"
    const val androidxPalette = "androidx.palette:palette:${Versions.androidx}"

    const val androidKtxAppCompat = "com.ivianuu.androidktx:appcompat:${Versions.androidKtx}"
    const val androidKtxCardView = "com.ivianuu.androidktx:cardview:${Versions.androidKtx}"
    const val androidKtxCore = "com.ivianuu.androidktx:core:${Versions.androidKtx}"
    const val androidKtxFragment = "com.ivianuu.androidktx:fragment:${Versions.androidKtx}"
    const val androidKtxLifecycle = "com.ivianuu.androidktx:lifecycle:${Versions.androidKtx}"
    const val androidKtxRecyclerView = "com.ivianuu.androidktx:recyclerview:${Versions.androidKtx}"
    const val androidKtxViewPager = "com.ivianuu.androidktx:viewpager:${Versions.androidKtx}"

    const val archLifecycleExtensions =
        "androidx.lifecycle:lifecycle-extensions:${Versions.androidxArch}"

    const val archWorkRuntime = "android.arch.work:work-runtime-ktx:${Versions.archWork}"

    const val assistedInject =
        "com.ivianuu.assistedinject:assistedinject:${Versions.assistedInject}"
    const val assistedInjectCompiler =
        "com.ivianuu.assistedinject:assistedinject-compiler:${Versions.assistedInject}"

    const val compass = "com.ivianuu.compass:compass:${Versions.compass}"
    const val compassAndroid = "com.ivianuu.compass:compass-android:${Versions.compass}"
    const val compassCompiler = "com.ivianuu.compass:compass-compiler:${Versions.compass}"
    const val compassDirector = "com.ivianuu.compass:compass-director:${Versions.compass}"
    const val compassFragment = "com.ivianuu.compass:compass-fragment:${Versions.compass}"

    const val contributor = "com.ivianuu.contributor:contributor:${Versions.contributor}"
    const val contributorCompiler =
        "com.ivianuu.contributor:contributor-compiler:${Versions.contributor}"
    const val contributorDirector =
        "com.ivianuu.contributor:contributor-director:${Versions.contributor}"
    const val contributorView = "com.ivianuu.contributor:contributor-view:${Versions.contributor}"

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

    const val director = "com.ivianuu.director:director:${Versions.director}"
    const val directorArchLifecycle =
        "com.ivianuu.director:director-arch-lifecycle:${Versions.director}"
    const val directorCommon = "com.ivianuu.director:director-common:${Versions.director}"
    const val directorDialog = "com.ivianuu.director:director-dialog:${Versions.director}"
    const val directorViewPager =
        "com.ivianuu.director:director-viewpager:${Versions.director}"

    const val epoxy = "com.airbnb.android:epoxy:${Versions.epoxy}"
    const val epoxyProcessor = "com.airbnb.android:epoxy-processor:${Versions.epoxy}"
    const val epoxyKtx = "com.ivianuu.epoxyktx:epoxyktx:${Versions.epoxyKtx}"
    const val epoxyPrefs = "com.ivianuu.epoxyprefs:epoxyprefs:${Versions.epoxyPrefs}"

    const val fabric = "com.crashlytics.sdk.android:crashlytics:${Versions.fabric}@aar"

    const val glide = "com.github.bumptech.glide:glide:${Versions.glide}"

    const val kotlinGradlePlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin}"
    const val kotlinStdLib = "org.jetbrains.kotlin:kotlin-stdlib-jdk7:${Versions.kotlin}"

    const val kPrefs = "com.ivianuu.kprefs:kprefs:${Versions.kPrefs}"
    const val kPrefsCoroutines = "com.ivianuu.kprefs:kprefs-coroutines:${Versions.kPrefs}"
    const val kPrefsLifecycle = "com.ivianuu.kprefs:kprefs-lifecycle:${Versions.kPrefs}"
    const val kPrefsRx = "com.ivianuu.kprefs:kprefs-rx:${Versions.kPrefs}"

    const val kSystemSettings =
        "com.ivianuu.ksystemsettings:ksystemsettings:${Versions.kSystemSettings}"
    const val kSystemSettingsCoroutines =
        "com.ivianuu.ksystemsettings:ksystemsettings-coroutines:${Versions.kSystemSettings}"
    const val kSystemSettingsLifecycle =
        "com.ivianuu.ksystemsettings:ksystemsettings-lifecycle:${Versions.kSystemSettings}"
    const val kSystemSettingsRx =
        "com.ivianuu.ksystemsettings:ksystemsettings-rx:${Versions.kSystemSettings}"

    const val legacyAnnotations =
        "com.android.support:support-annotations:${Versions.legacySupport}"

    const val liveEvent = "com.ivianuu.liveevent:liveevent:${Versions.liveEvent}"

    const val materialComponents =
        "com.google.android.material:material:${Versions.materialComponents}"
    const val materialComponentsKtx =
        "com.ivianuu.materialcomponentsktx:materialcomponentsktx:${Versions.materialComponentsKtx}"

    const val materialDialogsCore = "com.afollestad.material-dialogs:core:${Versions.materialDialogs}"
    const val materialDialogsCommons = "com.afollestad.material-dialogs:commons:${Versions.materialDialogs}"

    const val mavenGradlePlugin = "com.github.dcendents:android-maven-gradle-plugin:${Versions.mavenGradle}"

    const val r2GradlePlugin = "com.github.IVIanuu:r2:${Versions.r2}"

    const val rxAndroid = "io.reactivex.rxjava2:rxandroid:${Versions.rxAndroid}"
    const val rxAndroid2 = "com.ivianuu.rxandroid:rxandroid:${Versions.rxAndroid2}"
    const val rxJava = "io.reactivex.rxjava2:rxjava:${Versions.rxJava}"
    const val rxJavaKtx = "com.ivianuu.rxjavaktx:rxjavaktx:${Versions.rxJavaKtx}"
    const val rxKotlin = "io.reactivex.rxjava2:rxkotlin:${Versions.rxKotlin}"
    const val rxLifecycle = "com.github.IVIanuu:rx-lifecycle:${Versions.rxLifecycle}"

    const val superUser = "eu.chainfire:libsuperuser:${Versions.superUser}"

    const val stickyHeaders = "com.ivianuu.stickyheaders:stickyheaders:${Versions.stickyHeaders}"

    const val timber = "com.jakewharton.timber:timber:${Versions.timber}"
    const val timberKtx = "com.ivianuu.timberktx:timberktx:${Versions.timberKtx}"

    const val toasty = "com.github.GrenderG:Toasty:${Versions.toasty}"

    const val traveler = "com.ivianuu.traveler:traveler:${Versions.traveler}"
    const val travelerAndroid = "com.ivianuu.traveler:traveler-android:${Versions.traveler}"
    const val travelerDirector =
        "com.ivianuu.traveler:traveler-director:${Versions.traveler}"
    const val travelerFragment =
        "com.ivianuu.traveler:traveler-fragment:${Versions.traveler}"
    const val travelerLifecycle =
        "com.ivianuu.traveler:traveler-lifecycle:${Versions.traveler}"
    const val travelerPlugin =
        "com.ivianuu.traveler:traveler-plugin:${Versions.traveler}"
    const val travelerResult =
        "com.ivianuu.traveler:traveler-result:${Versions.traveler}"
}