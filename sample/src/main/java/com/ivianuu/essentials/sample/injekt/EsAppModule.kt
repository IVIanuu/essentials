package com.ivianuu.essentials.sample.injekt

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.ivianuu.essentials.app.EsApp
import com.ivianuu.essentials.util.EsPreferenceManager
import com.ivianuu.injekt.factory
import com.ivianuu.injekt.get
import com.ivianuu.injekt.module
import com.ivianuu.injekt.single
import com.ivianuu.kprefs.KPrefs
import com.ivianuu.ksettings.KSettings

fun esAppModule() = module {
    factory { get<EsApp>() as Application }
    factory { get<Application>() as Context }

    factory { EsPreferenceManager.getDefaultSharedPreferences(get()) }

    single { KPrefs(get<SharedPreferences>()) }

    single { KSettings(get<Context>()) }

    single { get<Context>().packageManager }
}