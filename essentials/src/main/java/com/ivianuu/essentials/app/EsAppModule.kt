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

package com.ivianuu.essentials.app

import android.content.SharedPreferences
import com.ivianuu.injekt.android.context
import com.ivianuu.injekt.android.sharedPreferences
import com.ivianuu.injekt.factory
import com.ivianuu.injekt.get
import com.ivianuu.injekt.module
import com.ivianuu.injekt.single
import com.ivianuu.kprefs.KPrefs
import com.ivianuu.ksettings.KSettings

/**
 * Basic app dependencies such as preferences or package manager
 */
fun esAppModule(packageName: String) = module {
    sharedPreferences(packageName + "_preferences")
    single { KPrefs(get<SharedPreferences>()) }
    single { KSettings(context()) }
    factory { context().packageManager!! }
}