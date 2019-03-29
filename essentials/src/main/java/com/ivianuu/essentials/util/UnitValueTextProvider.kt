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

package com.ivianuu.essentials.util

import android.content.Context
import com.ivianuu.essentials.R
import com.ivianuu.essentials.util.UnitValueTextProvider.Unit.DP
import com.ivianuu.essentials.util.UnitValueTextProvider.Unit.MILLIS
import com.ivianuu.essentials.util.UnitValueTextProvider.Unit.PERCENTAGE
import com.ivianuu.essentials.util.UnitValueTextProvider.Unit.PX
import com.ivianuu.essentials.util.UnitValueTextProvider.Unit.SECONDS
import com.ivianuu.kommon.core.content.string
import com.ivianuu.listprefs.SeekBarPreferenceModel
import com.ivianuu.listprefs.valueTextProvider

class UnitValueTextProvider(
    private val context: Context,
    private val unit: Unit
) : (Int) -> String {

    override fun invoke(value: Int) = when (unit) {
        DP -> context.string(R.string.es_seek_bar_pref_format_dp, value)
        MILLIS -> context.string(R.string.es_seek_bar_pref_format_millis, value)
        PERCENTAGE -> context.string(R.string.es_seek_bar_pref_format_percentage, value)
        PX -> context.string(R.string.es_seek_bar_pref_format_px, value)
        SECONDS -> context.string(R.string.es_seek_bar_pref_format_seconds, value)
    }

    enum class Unit {
        DP, MILLIS, PERCENTAGE, PX, SECONDS
    }
}

fun SeekBarPreferenceModel.unitValueTextProvider(unit: UnitValueTextProvider.Unit) {
    valueTextProvider(UnitValueTextProvider(context, unit))
}