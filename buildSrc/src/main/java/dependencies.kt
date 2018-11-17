@file:Suppress("ClassName", "unused")

object Build {
    const val applicationId = "com.ivianuu.essentials.sample"
    const val buildToolsVersion = "28.0.3"

    const val compileSdk = 28
    const val minSdk = 21
    const val targetSdk = 28
    const val versionCode = 1
    const val versionName = "0.0.1"
}

object
Versions {
    const val androidGradlePlugin = "3.2.1"

    const val androidxArch = "2.0.0"
    const val androidxActivity = "1.0.0-alpha01"
    const val androidxAppCompat = "1.0.2"
    const val androidxCardView = "1.0.0"
    const val androidxCore = "1.0.0"
    const val androidxFragment = "1.1.0-alpha01"
    const val androidxPalette = "1.0.0"

    const val androidxTestCore = "1.0.0"
    const val androidxTestJunit = "1.0.0"
    const val androidxTestRules = "1.1.0"
    const val androidxTestRunner = "1.1.0"

    const val androidKtx = "0cf0a07f49"

    const val archWork = "1.0.0-alpha11"

    const val assistedInject = "18c716578f"

    const val constraintLayout = "1.1.3"
    const val contributor = "efa69d138b"
    const val coroutines = "1.0.0"
    const val dagger = "2.19"
    const val director = "7228780b94"
    const val epoxy = "2.19.0"
    const val epoxyKtx = "9dde1f5a9c"
    const val epoxyPrefs = "f10b99d7c1"
    const val glide = "4.8.0"
    const val injectors = "995ca52397"
    const val junit = "4.12"
    const val kotlin = "1.3.10"
    const val kPrefs = "79aac06ee7"
    const val kSettings = "70219d641b"
    const val legacySupport = "28.0.0"
    const val liveEvent = "1e265df911"
    const val mavenGradle = "2.1"
    const val materialComponents = "1.0.0"
    const val materialComponentsKtx = "c4c5dda45a"
    const val materialDialogs = "0.9.6.0"
    const val mockitoKotlin = "2.0.0"
    const val r2 = "06e702d39f"
    const val roboelectric = "4.0.2"
    const val rxAndroid = "2.1.0"
    const val rxJava = "2.2.2"
    const val rxJavaKtx = "015125e952"
    const val rxKotlin = "2.3.0"
    const val scopes = "d9e4485f16"
    const val superUser = "1.0.0.+"
    const val stateStore = "6063560b05"
    const val stickyHeaders = "44c5192160"
    const val timber = "4.7.1"
    const val timberKtx = "f7547da781"
    const val toasty = "1.3.0"
    const val traveler = "e1cb60c864"
}

object Deps {
    const val androidGradlePlugin = "com.android.tools.build:gradle:${Versions.androidGradlePlugin}"

    const val androidxActivity = "androidx.activity:activity:${Versions.androidxActivity}"
    const val androidxAppCompat = "androidx.appcompat:appcompat:${Versions.androidxAppCompat}"
    const val androidxCardView = "androidx.cardview:cardview:${Versions.androidxCardView}"
    const val androidxCoreKtx = "androidx.core:core-ktx:${Versions.androidxCore}"
    const val androidxFragment = "androidx.fragment:fragment:${Versions.androidxFragment}"
    const val androidxFragmentTesting =
        "androidx.fragment:fragment-testing:${Versions.androidxFragment}"

    const val androidxPalette = "androidx.palette:palette:${Versions.androidxPalette}"

    const val androidxTestCore = "androidx.test:core:${Versions.androidxTestCore}"
    const val androidxTestJunit = "androidx.test.ext:junit:${Versions.androidxTestJunit}"
    const val androidxTestRules = "androidx.test:rules:${Versions.androidxTestRules}"
    const val androidxTestRunner = "androidx.test:runner:${Versions.androidxTestRunner}"

    const val androidKtxAppCompat =
        "com.github.IVIanuu.android-ktx:androidktx-appcompat:${Versions.androidKtx}"
    const val androidKtxCore = "com.github.IVIanuu.android-ktx:androidktx-core:${Versions.androidKtx}"
    const val androidKtxFragment = "com.github.IVIanuu.android-ktx:androidktx-fragment:${Versions.androidKtx}"
    const val androidKtxLifecycle =
        "com.github.IVIanuu.android-ktx:androidktx-lifecycle:${Versions.androidKtx}"
    const val androidKtxRecyclerView =
        "com.github.IVIanuu.android-ktx:androidktx-recyclerview:${Versions.androidKtx}"
    const val androidKtxViewPager =
        "com.github.IVIanuu.android-ktx:androidktx-viewpager:${Versions.androidKtx}"

    const val archLifecycleExtensions =
        "androidx.lifecycle:lifecycle-extensions:${Versions.androidxArch}"

    const val archWorkRuntime = "android.arch.work:work-runtime-ktx:${Versions.archWork}"

    const val assistedInject =
        "com.github.IVIanuu.assisted-inject:assistedinject:${Versions.assistedInject}"
    const val assistedInjectCompiler =
        "com.github.IVIanuu.assisted-inject:assistedinject-compiler:${Versions.assistedInject}"

    const val contributor = "com.github.IVIanuu.contributor:contributor:${Versions.contributor}"
    const val contributorCompiler =
        "com.github.IVIanuu.contributor:contributor-compiler:${Versions.contributor}"
    const val contributorView =
        "com.github.IVIanuu.contributor:contributor-view:${Versions.contributor}"

    const val constraintLayout =
        "androidx.constraintlayout:constraintlayout:${Versions.constraintLayout}"

    const val coroutinesAndroid =
        "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.coroutines}"
    const val coroutinesCore =
        "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.coroutines}"
    const val coroutinesRxJava =
        "org.jetbrains.kotlinx:kotlinx-coroutines-rx2:${Versions.coroutines}"

    const val dagger = "com.google.dagger:dagger:${Versions.dagger}"
    const val daggerCompiler = "com.google.dagger:dagger-compiler:${Versions.dagger}"

    const val director = "com.github.IVIanuu.director:director:${Versions.director}"
    const val directorArchLifecycle =
        "com.github.IVIanuu.director:director-arch-lifecycle:${Versions.director}"
    const val directorCommon = "com.github.IVIanuu.director:director-common:${Versions.director}"
    const val directorDialog = "com.github.IVIanuu.director:director-dialog:${Versions.director}"
    const val directorScopes = "com.github.IVIanuu.director:director-scopes:${Versions.director}"
    const val directorTesting =
        "com.github.IVIanuu.director:director-testing:${Versions.director}"
    const val directorTraveler =
        "com.github.IVIanuu.director:director-traveler:${Versions.director}"
    const val directorViewPager =
        "com.github.IVIanuu.director:director-viewpager:${Versions.director}"

    const val epoxy = "com.airbnb.android:epoxy:${Versions.epoxy}"
    const val epoxyProcessor = "com.airbnb.android:epoxy-processor:${Versions.epoxy}"
    const val epoxyKtx = "com.github.IVIanuu:epoxy-ktx:${Versions.epoxyKtx}"
    const val epoxyPrefs = "com.github.IVIanuu:epoxy-prefs:${Versions.epoxyPrefs}"

    const val glide = "com.github.bumptech.glide:glide:${Versions.glide}"

    const val injectors = "com.github.IVIanuu.injectors:injectors:${Versions.injectors}"
    const val injectorsAndroid =
        "com.github.IVIanuu.injectors:injectors-android:${Versions.injectors}"
    const val injectorsCompiler =
        "com.github.IVIanuu.injectors:injectors-compiler:${Versions.injectors}"
    const val injectorsFragment =
        "com.github.IVIanuu.injectors:injectors-fragment:${Versions.injectors}"

    const val junit = "junit:junit:${Versions.junit}"

    const val kotlinGradlePlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin}"
    const val kotlinStdLib = "org.jetbrains.kotlin:kotlin-stdlib-jdk7:${Versions.kotlin}"

    const val kPrefs = "com.github.IVIanuu.kprefs:kprefs:${Versions.kPrefs}"
    const val kPrefsCoroutines = "com.github.IVIanuu.kprefs:kprefs-coroutines:${Versions.kPrefs}"
    const val kPrefsLifecycle = "com.github.IVIanuu.kprefs:kprefs-lifecycle:${Versions.kPrefs}"
    const val kPrefsRx = "com.github.IVIanuu.kprefs:kprefs-rx:${Versions.kPrefs}"

    const val kSettings =
        "com.github.IVIanuu.ksettings:ksettings:${Versions.kSettings}"
    const val kSettingsCoroutines =
        "com.github.IVIanuu.ksettings:ksettings-coroutines:${Versions.kSettings}"
    const val kSettingsLifecycle =
        "com.github.IVIanuu.ksettings:ksettings-lifecycle:${Versions.kSettings}"
    const val kSettingsRx =
        "com.github.IVIanuu.ksettings:ksettings-rx:${Versions.kSettings}"

    const val legacyAnnotations =
        "com.android.support:support-annotations:${Versions.legacySupport}"

    const val liveEvent = "com.github.IVIanuu:liveevent:${Versions.liveEvent}"

    const val materialComponents =
        "com.google.android.material:material:${Versions.materialComponents}"
    const val materialComponentsKtx =
        "com.github.IVIanuu:material-components-ktx:${Versions.materialComponentsKtx}"

    const val materialDialogsCore = "com.afollestad.material-dialogs:core:${Versions.materialDialogs}"
    const val materialDialogsCommons = "com.afollestad.material-dialogs:commons:${Versions.materialDialogs}"

    const val mavenGradlePlugin = "com.github.dcendents:android-maven-gradle-plugin:${Versions.mavenGradle}"

    const val mockitoKotlin = "com.nhaarman.mockitokotlin2:mockito-kotlin:${Versions.mockitoKotlin}"

    const val r2GradlePlugin = "com.github.IVIanuu:r2:${Versions.r2}"

    const val roboelectric = "org.robolectric:robolectric:${Versions.roboelectric}"

    const val rxAndroid = "io.reactivex.rxjava2:rxandroid:${Versions.rxAndroid}"
    const val rxJava = "io.reactivex.rxjava2:rxjava:${Versions.rxJava}"
    const val rxJavaKtx = "com.github.IVIanuu:rx-java-ktx:${Versions.rxJavaKtx}"
    const val rxKotlin = "io.reactivex.rxjava2:rxkotlin:${Versions.rxKotlin}"

    const val stateStore = "com.github.IVIanuu.statestore:statestore:${Versions.stateStore}"
    const val stateStoreAndroid =
        "com.github.IVIanuu.statestore:statestore-android:${Versions.stateStore}"
    const val stateStoreCoroutines =
        "com.github.IVIanuu.statestore:statestore-coroutines:${Versions.stateStore}"
    const val stateStoreLifecycle =
        "com.github.IVIanuu.statestore:statestore-lifecycle:${Versions.stateStore}"
    const val stateStoreRx = "com.github.IVIanuu.statestore:statestore-rx:${Versions.stateStore}"

    const val scopes = "com.github.IVIanuu.scopes:scopes:${Versions.scopes}"
    const val scopesAndroid = "com.github.IVIanuu.scopes:scopes-android:${Versions.scopes}"
    const val scopesArchLifecycle =
        "com.github.IVIanuu.scopes:scopes-arch-lifecycle:${Versions.scopes}"
    const val scopesArchLifecycleFragment =
        "com.github.IVIanuu.scopes:scopes-arch-lifecycle-fragment:${Versions.scopes}"
    const val scopesCommon = "com.github.IVIanuu.scopes:scopes-common:${Versions.scopes}"
    const val scopesCoroutines = "com.github.IVIanuu.scopes:scopes-coroutines:${Versions.scopes}"
    const val scopesRx = "com.github.IVIanuu.scopes:scopes-rx:${Versions.scopes}"

    const val superUser = "eu.chainfire:libsuperuser:${Versions.superUser}"

    const val stickyHeaders = "com.github.IVIanuu:sticky-headers:${Versions.stickyHeaders}"

    const val timber = "com.jakewharton.timber:timber:${Versions.timber}"
    const val timberKtx = "com.github.IVIanuu:timber-ktx:${Versions.timberKtx}"

    const val toasty = "com.github.GrenderG:Toasty:${Versions.toasty}"

    const val traveler = "com.github.IVIanuu.traveler:traveler:${Versions.traveler}"
    const val travelerAndroid = "com.github.IVIanuu.traveler:traveler-android:${Versions.traveler}"
    const val travelerCommon =
        "com.github.IVIanuu.traveler:traveler-common:${Versions.traveler}"
    const val travelerFragment =
        "com.github.IVIanuu.traveler:traveler-fragment:${Versions.traveler}"
    const val travelerLifecycle =
        "com.github.IVIanuu.traveler:traveler-lifecycle:${Versions.traveler}"
    const val travelerResult =
        "com.github.IVIanuu.traveler:traveler-result:${Versions.traveler}"
}