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

import com.ivianuu.essentials.R
import com.ivianuu.injekt.Inject
import com.ivianuu.injekt.android.ApplicationScope
import com.ivianuu.kprefs.KPrefs
import com.ivianuu.kprefs.common.PrefValueHolder
import com.ivianuu.kprefs.common.enumString

@Inject
@ApplicationScope
class TwilightPrefs(prefs: KPrefs) {
    val twilightMode = prefs.enumString("twilight_mode",
        TwilightMode.System
    )
}

enum class TwilightMode(
    override val value: String,
    val titleRes: Int,
    val descRes: Int
) : PrefValueHolder<String> {
    System(
        value = "system",
        titleRes = R.string.es_twilight_mode_system,
        descRes = R.string.es_twilight_mode_system_desc
    ),
    Light(
        value = "light",
        titleRes = R.string.es_twilight_mode_light,
        descRes = R.string.es_twilight_mode_light_desc
    ),
    Dark(
        value = "dark",
        titleRes = R.string.es_twilight_mode_dark,
        descRes = R.string.es_twilight_mode_dark_desc
    ),
    Battery(
        value = "battery",
        titleRes = R.string.es_twilight_mode_battery,
        descRes = R.string.es_twilight_mode_battery_desc
    ),
    Time(
        value = "time",
        titleRes = R.string.es_twilight_mode_time,
        descRes = R.string.es_twilight_mode_time_desc
    )
}