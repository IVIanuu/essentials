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

package com.ivianuu.essentials.hidenavbar

import android.content.SharedPreferences
import com.ivianuu.essentials.app.appService
import com.ivianuu.injekt.android.sharedPreferences
import com.ivianuu.injekt.factory
import com.ivianuu.injekt.get
import com.ivianuu.injekt.module
import com.ivianuu.injekt.single
import com.ivianuu.kprefs.KPrefs

object NavBar

const val NAV_BAR_SHARED_PREFS_NAME = "com.ivianuu.essentials.hidenavbar.prefs"

/**
 * Provides nav bar related dependencies
 */
val esNavBarModule = module {
    appService { NavBarController(get(), get(), get(), get(), get(), get(), get(), get()) }

    sharedPreferences(NAV_BAR_SHARED_PREFS_NAME, name = NavBar)
    single(NavBar) { KPrefs(get<SharedPreferences>(NavBar)) }
    single { NavBarPrefs(get(NavBar)) }

    factory { DisplayRotationProvider(get()) }
    factory { OverscanHelper() }
    factory { NonSdkInterfacesHelper(get()) }
    factory { ScreenStateProvider(get()) }
}