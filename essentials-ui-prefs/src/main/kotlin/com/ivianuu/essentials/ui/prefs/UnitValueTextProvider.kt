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

package com.ivianuu.essentials.ui.prefs

import android.content.Context
import com.ivianuu.essentials.ui.prefs.UnitValueTextProvider.Unit.DP
import com.ivianuu.essentials.ui.prefs.UnitValueTextProvider.Unit.MILLIS
import com.ivianuu.essentials.ui.prefs.UnitValueTextProvider.Unit.PERCENTAGE
import com.ivianuu.essentials.ui.prefs.UnitValueTextProvider.Unit.PX
import com.ivianuu.essentials.ui.prefs.UnitValueTextProvider.Unit.SECONDS

class UnitValueTextProvider(
    private val context: Context,
    private val unit: Unit
) : (String) -> String {

    override fun invoke(value: String) = when (unit) {
        DP -> context.getString(R.string.es_seek_bar_pref_format_dp, value)
        MILLIS -> context.getString(R.string.es_seek_bar_pref_format_millis, value)
        PERCENTAGE -> context.getString(R.string.es_seek_bar_pref_format_percentage, value)
        PX -> context.getString(R.string.es_seek_bar_pref_format_px, value)
        SECONDS -> context.getString(R.string.es_seek_bar_pref_format_seconds, value)
    }

    enum class Unit {
        DP, MILLIS, PERCENTAGE, PX, SECONDS
    }
}
