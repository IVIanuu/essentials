package com.ivianuu.essentials.util

import android.content.Context
import com.ivianuu.epoxyprefs.SeekBarPreferenceModel
import com.ivianuu.essentials.R

class UnitValueTextProvider(
    private val context: Context,
    private val unit: Unit
) : SeekBarPreferenceModel.ValueTextProvider {

    override fun getText(value: Int) = when (unit) {
        Unit.DP -> context.getString(R.string.seek_bar_pref_format_dp, value)
        Unit.MILLIS -> context.getString(R.string.seek_bar_pref_format_millis, value)
        Unit.PERCENTAGE -> context.getString(R.string.seek_bar_pref_format_percentage, value)
        Unit.PX -> context.getString(R.string.seek_bar_pref_format_px, value)
        Unit.SECONDS -> context.getString(R.string.seek_bar_pref_format_seconds, value)
    }

    enum class Unit {
        DP, MILLIS, PERCENTAGE, PX, SECONDS
    }
}

inline fun SeekBarPreferenceModel.Builder.unitValueTextProvider(unit: UnitValueTextProvider.Unit) =
    valueTextProvider(UnitValueTextProvider(context, unit))