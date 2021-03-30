/*
 * Copyright 2020 Manuel Wrage
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

package com.ivianuu.essentials.twilight.data

import com.ivianuu.essentials.twilight.R

enum class TwilightMode(
    val titleRes: Int,
    val descRes: Int
) {
    SYSTEM(
        titleRes = R.string.es_twilight_mode_system,
        descRes = R.string.es_twilight_mode_system_desc
    ),
    LIGHT(
        titleRes = R.string.es_twilight_mode_light,
        descRes = R.string.es_twilight_mode_light_desc
    ),
    DARK(
        titleRes = R.string.es_twilight_mode_dark,
        descRes = R.string.es_twilight_mode_dark_desc
    ),
    BATTERY(
        titleRes = R.string.es_twilight_mode_battery,
        descRes = R.string.es_twilight_mode_battery_desc
    ),
    TIME(
        titleRes = R.string.es_twilight_mode_time,
        descRes = R.string.es_twilight_mode_time_desc
    )
}
