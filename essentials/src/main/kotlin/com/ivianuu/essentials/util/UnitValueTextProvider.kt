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

package com.ivianuu.essentials.util

import android.content.Context
import com.ivianuu.epoxyprefs.SeekBarPreferenceModel
import com.ivianuu.essentials.R
import com.ivianuu.essentials.util.UnitValueTextProvider.Unit.Dp
import com.ivianuu.essentials.util.UnitValueTextProvider.Unit.Millis
import com.ivianuu.essentials.util.UnitValueTextProvider.Unit.Percentage
import com.ivianuu.essentials.util.UnitValueTextProvider.Unit.Px
import com.ivianuu.essentials.util.UnitValueTextProvider.Unit.Seconds

class UnitValueTextProvider(
    private val context: Context,
    private val unit: Unit
) : (String) -> String {

    override fun invoke(value: String) = when (unit) {
        Dp -> context.string(R.string.es_seek_bar_pref_format_dp, value)
        Millis -> context.string(R.string.es_seek_bar_pref_format_millis, value)
        Percentage -> context.string(R.string.es_seek_bar_pref_format_percentage, value)
        Px -> context.string(R.string.es_seek_bar_pref_format_px, value)
        Seconds -> context.string(R.string.es_seek_bar_pref_format_seconds, value)
    }

    enum class Unit {
        Dp, Millis, Percentage, Px, Seconds
    }
}

fun SeekBarPreferenceModel.Builder.unitValueTextProvider(
    context: Context,
    unit: UnitValueTextProvider.Unit
) {
    valueTextProvider { UnitValueTextProvider(context, unit).invoke(it.toString()) }
}
