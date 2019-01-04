@file:Suppress("ClassName", "unused")

object Build {
    const val applicationId = "com.ivianuu.essentials.sample"
    const val buildToolsVersion = "28.0.3"

    const val compileSdk = 28
    const val minSdk = 21
    const val targetSdk = 28
    const val targetSdkSample = 27
    const val versionCode = 1
    const val versionName = "0.0.1"
}

object Versions {
    const val androidGradlePlugin = "3.2.1"

    const val androidxAppCompat = "1.0.2"
    const val androidxCardView = "1.0.0"
    const val androidxCore = "1.0.1"
    const val androidxLifecycle = "2.0.0"
    const val androidxWork = "1.0.0-beta01"

    const val androidxTestCore = "1.0.0"
    const val androidxTestJunit = "1.0.0"
    const val androidxTestRules = "1.1.0"
    const val androidxTestRunner = "1.1.0"

    const val butterknife = "9.0.0-rc3"

    const val constraintLayout = "2.0.0-alpha2"
    const val coroutines = "1.1.0"
    const val director = "daa4ec173f"
    const val epoxy = "2.19.0"
    const val epoxyKtx = "9dde1f5a9c"
    const val epoxyPrefs = "0a007754c4"
    const val glide = "4.8.0"
    const val injekt = "20bc5a9c3c"
    const val junit = "4.12"
    const val kommon = "6c51279983"
    const val kotlin = "1.3.11"
    const val kPrefs = "ff00f64c9b"
    const val kSettings = "4b27df8a77"
    const val legacySupport = "28.0.0"
    const val liveEvent = "1e265df911"
    const val mavenGradle = "2.1"
    const val materialComponents = "1.1.0-alpha02"
    const val materialDialogs = "2.0.0-rc1"
    const val mockitoKotlin = "2.0.0"
    const val r2 = "06e702d39f"
    const val roboelectric = "4.0.2"
    const val rxAndroid = "2.1.0"
    const val rxJava = "2.2.2"
    const val rxJavaKtx = "015125e952"
    const val rxKotlin = "2.3.0"
    const val scopes = "6c630efcd6"
    const val superUser = "1.0.0.+"
    const val stateStore = "19518b13f6"
    const val timber = "4.7.1"
    const val timberKtx = "f7547da781"
    const val traveler = "ab66233b33"
}

object Deps {
    const val androidGradlePlugin = "com.android.tools.build:gradle:${Versions.androidGradlePlugin}"

    const val androidxAppCompat = "androidx.appcompat:appcompat:${Versions.androidxAppCompat}"
    const val androidxCardView = "androidx.cardview:cardview:${Versions.androidxCardView}"
    const val androidxCoreKtx = "androidx.core:core-ktx:${Versions.androidxCore}"
    const val androidxLifecycleExtensions =
        "androidx.lifecycle:lifecycle-extensions:${Versions.androidxLifecycle}"
    const val androidxWorkRuntime = "android.arch.work:work-runtime-ktx:${Versions.androidxWork}"

    const val androidxTestCore = "androidx.test:core:${Versions.androidxTestCore}"
    const val androidxTestJunit = "androidx.test.ext:junit:${Versions.androidxTestJunit}"
    const val androidxTestRules = "androidx.test:rules:${Versions.androidxTestRules}"
    const val androidxTestRunner = "androidx.test:runner:${Versions.androidxTestRunner}"

    const val butterknifeGradlePlugin =
        "com.jakewharton:butterknife-gradle-plugin:${Versions.butterknife}"

    const val constraintLayout =
        "androidx.constraintlayout:constraintlayout:${Versions.constraintLayout}"

    const val coroutinesAndroid =
        "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.coroutines}"
    const val coroutinesCore =
        "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.coroutines}"
    const val coroutinesRxJava =
        "org.jetbrains.kotlinx:kotlinx-coroutines-rx2:${Versions.coroutines}"

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

    const val injekt = "com.github.IVIanuu.injekt:injekt:${Versions.injekt}"
    const val injektAndroid = "com.github.IVIanuu.injekt:injekt-android:${Versions.injekt}"
    const val injektMultiBinding =
        "com.github.IVIanuu.injekt:injekt-multibinding:${Versions.injekt}"

    const val junit = "junit:junit:${Versions.junit}"

    const val kommonAppCompat =
        "com.github.IVIanuu.kommon:kommon-appcompat:${Versions.kommon}"
    const val kommonCore = "com.github.IVIanuu.kommon:kommon-core:${Versions.kommon}"
    const val kommonLifecycle =
        "com.github.IVIanuu.kommon:kommon-lifecycle:${Versions.kommon}"
    const val kommonMaterial =
        "com.github.IVIanuu.kommon:kommon-material:${Versions.kommon}"
    const val kommonRecyclerView =
        "com.github.IVIanuu.kommon:kommon-recyclerview:${Versions.kommon}"
    const val kommonViewPager =
        "com.github.IVIanuu.kommon:kommon-viewpager:${Versions.kommon}"
    
    const val kotlinGradlePlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin}"
    const val kotlinStdLib = "org.jetbrains.kotlin:kotlin-stdlib-jdk7:${Versions.kotlin}"

    const val kPrefs = "com.github.IVIanuu.kprefs:kprefs:${Versions.kPrefs}"
    const val kPrefsCoroutines = "com.github.IVIanuu.kprefs:kprefs-coroutines:${Versions.kPrefs}"
    const val kPrefsLiveData = "com.github.IVIanuu.kprefs:kprefs-livedata:${Versions.kPrefs}"
    const val kPrefsRx = "com.github.IVIanuu.kprefs:kprefs-rx:${Versions.kPrefs}"

    const val kSettings =
        "com.github.IVIanuu.ksettings:ksettings:${Versions.kSettings}"
    const val kSettingsCoroutines =
        "com.github.IVIanuu.ksettings:ksettings-coroutines:${Versions.kSettings}"
    const val kSettingsLiveData =
        "com.github.IVIanuu.ksettings:ksettings-livedata:${Versions.kSettings}"
    const val kSettingsRx =
        "com.github.IVIanuu.ksettings:ksettings-rx:${Versions.kSettings}"

    const val legacyAnnotations =
        "com.android.support:support-annotations:${Versions.legacySupport}"

    const val liveEvent = "com.github.IVIanuu:liveevent:${Versions.liveEvent}"

    const val materialComponents =
        "com.google.android.material:material:${Versions.materialComponents}"

    const val materialDialogsCore =
        "com.afollestad.material-dialogs:core:${Versions.materialDialogs}"
    const val materialDialogsInput =
        "com.afollestad.material-dialogs:input:${Versions.materialDialogs}"
    const val materialDialogsColor =
        "com.afollestad.material-dialogs:color:${Versions.materialDialogs}"

    const val mavenGradlePlugin = "com.github.dcendents:android-maven-gradle-plugin:${Versions.mavenGradle}"

    const val mockitoKotlin = "com.nhaarman.mockitokotlin2:mockito-kotlin:${Versions.mockitoKotlin}"

    const val r2GradlePlugin = "com.github.IVIanuu:r2:${Versions.r2}"

    const val roboelectric = "org.robolectric:robolectric:${Versions.roboelectric}"

    const val rxAndroid = "io.reactivex.rxjava2:rxandroid:${Versions.rxAndroid}"
    const val rxJava = "io.reactivex.rxjava2:rxjava:${Versions.rxJava}"
    const val rxJavaKtx = "com.github.IVIanuu:rx-java-ktx:${Versions.rxJavaKtx}"
    const val rxKotlin = "io.reactivex.rxjava2:rxkotlin:${Versions.rxKotlin}"

    const val scopes = "com.github.IVIanuu.scopes:scopes:${Versions.scopes}"
    const val scopesAndroid = "com.github.IVIanuu.scopes:scopes-android:${Versions.scopes}"
    const val scopesAndroidLifecycle =
        "com.github.IVIanuu.scopes:scopes-android-lifecycle:${Versions.scopes}"
    const val scopesCommon = "com.github.IVIanuu.scopes:scopes-common:${Versions.scopes}"
    const val scopesCoroutines = "com.github.IVIanuu.scopes:scopes-coroutines:${Versions.scopes}"
    const val scopesLiveData =
        "com.github.IVIanuu.scopes:scopes-livedata:${Versions.scopes}"
    const val scopesRx = "com.github.IVIanuu.scopes:scopes-rx:${Versions.scopes}"

    const val stateStore = "com.github.IVIanuu.statestore:statestore:${Versions.stateStore}"
    const val stateStoreAndroid =
        "com.github.IVIanuu.statestore:statestore-android:${Versions.stateStore}"
    const val stateStoreCoroutines =
        "com.github.IVIanuu.statestore:statestore-coroutines:${Versions.stateStore}"
    const val stateStoreLiveData =
        "com.github.IVIanuu.statestore:statestore-livedata:${Versions.stateStore}"
    const val stateStoreRx = "com.github.IVIanuu.statestore:statestore-rx:${Versions.stateStore}"

    const val superUser = "eu.chainfire:libsuperuser:${Versions.superUser}"

    const val timber = "com.jakewharton.timber:timber:${Versions.timber}"
    const val timberKtx = "com.github.IVIanuu:timber-ktx:${Versions.timberKtx}"

    const val traveler = "com.github.IVIanuu.traveler:traveler:${Versions.traveler}"
    const val travelerAndroid = "com.github.IVIanuu.traveler:traveler-android:${Versions.traveler}"
    const val travelerCommon =
        "com.github.IVIanuu.traveler:traveler-common:${Versions.traveler}"
    const val travelerLifecycle =
        "com.github.IVIanuu.traveler:traveler-lifecycle:${Versions.traveler}"
}