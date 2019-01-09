package com.ivianuu.essentials.util

import android.content.Context
import com.ivianuu.epoxyprefs.SeekBarPreferenceModel
import com.ivianuu.essentials.R
import com.ivianuu.kommon.core.content.string

class UnitValueTextProvider(
    private val context: Context,
    private val unit: Unit
) : (Int) -> String {

    override fun invoke(value: Int) = when (unit) {
        Unit.DP -> context.string(R.string.es_seek_bar_pref_format_dp, value)
        Unit.MILLIS -> context.string(R.string.es_seek_bar_pref_format_millis, value)
        Unit.PERCENTAGE -> context.string(R.string.es_seek_bar_pref_format_percentage, value)
        Unit.PX -> context.string(R.string.es_seek_bar_pref_format_px, value)
        Unit.SECONDS -> context.string(R.string.es_seek_bar_pref_format_seconds, value)
    }

    enum class Unit {
        DP, MILLIS, PERCENTAGE, PX, SECONDS
    }
}

fun SeekBarPreferenceModel.Builder.unitValueTextProvider(unit: UnitValueTextProvider.Unit) {
    valueTextProvider(UnitValueTextProvider(context, unit))
}