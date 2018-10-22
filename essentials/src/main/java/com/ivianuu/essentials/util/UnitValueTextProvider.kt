package com.ivianuu.essentials.util

import android.content.Context
import com.ivianuu.androidktx.core.content.string
import com.ivianuu.epoxyprefs.SeekBarPreferenceModel
import com.ivianuu.essentials.R

class UnitValueTextProvider(
    private val context: Context,
    private val unit: Unit
) : SeekBarPreferenceModel.ValueTextProvider {

    override fun getText(value: Int) = when (unit) {
        Unit.DP -> context.string(R.string.seek_bar_pref_format_dp, value)
        Unit.MILLIS -> context.string(R.string.seek_bar_pref_format_millis, value)
        Unit.PERCENTAGE -> context.string(R.string.seek_bar_pref_format_percentage, value)
        Unit.PX -> context.string(R.string.seek_bar_pref_format_px, value)
        Unit.SECONDS -> context.string(R.string.seek_bar_pref_format_seconds, value)
    }

    enum class Unit {
        DP, MILLIS, PERCENTAGE, PX, SECONDS
    }
}

fun SeekBarPreferenceModel.Builder.unitValueTextProvider(unit: UnitValueTextProvider.Unit) =
    valueTextProvider(UnitValueTextProvider(context, unit))