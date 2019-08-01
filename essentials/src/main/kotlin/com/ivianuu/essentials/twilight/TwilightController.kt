/*
 * Copyright 2019 Manuel Wrage
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

package com.ivianuu.essentials.twilight

import androidx.appcompat.app.AppCompatDelegate
import com.ivianuu.essentials.app.AppService
import com.ivianuu.essentials.util.AppDispatchers
import com.ivianuu.essentials.util.flowWith
import com.ivianuu.injekt.Inject
import com.ivianuu.injekt.android.ApplicationScope
import com.ivianuu.kprefs.coroutines.asFlow
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@Inject
@ApplicationScope
class TwilightController(
    private val dispatchers: AppDispatchers,
    private val twilightPrefs: TwilightPrefs
) : AppService {

    init {
        twilightPrefs.twilightMode.asFlow()
            .flowWith(dispatchers.main)
            .onEach { mode ->
                AppCompatDelegate.setDefaultNightMode(
                    when (mode) {
                        TwilightMode.LIGHT -> AppCompatDelegate.MODE_NIGHT_NO
                        TwilightMode.DARK -> AppCompatDelegate.MODE_NIGHT_YES
                        TwilightMode.BATTERY -> AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY
                        TwilightMode.TIME -> AppCompatDelegate.MODE_NIGHT_AUTO_TIME
                        TwilightMode.SYSTEM -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                    }
                )
            }
            .launchIn(GlobalScope)
    }

}