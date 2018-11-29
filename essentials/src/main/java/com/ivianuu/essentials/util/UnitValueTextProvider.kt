package com.ivianuu.essentials.util

import android.content.Context
import com.ivianuu.epoxyprefs.SeekBarPreferenceModel
import com.ivianuu.essentials.R
import com.ivianuu.kommon.core.content.string

class UnitValueTextProvider(
    private val context: Context,
    private val unit: Unit
) : (Int) -> String {

    override fun invoke(p1: Int) = when (unit) {
        Unit.DP -> context.string(R.string.seek_bar_pref_format_dp, p1)
        Unit.MILLIS -> context.string(R.string.seek_bar_pref_format_millis, p1)
        Unit.PERCENTAGE -> context.string(R.string.seek_bar_pref_format_percentage, p1)
        Unit.PX -> context.string(R.string.seek_bar_pref_format_px, p1)
        Unit.SECONDS -> context.string(R.string.seek_bar_pref_format_seconds, p1)
    }

    enum class Unit {
        DP, MILLIS, PERCENTAGE, PX, SECONDS
    }
}

fun SeekBarPreferenceModel.Builder.unitValueTextProvider(unit: UnitValueTextProvider.Unit) =
    valueTextProvider(UnitValueTextProvider(context, unit))