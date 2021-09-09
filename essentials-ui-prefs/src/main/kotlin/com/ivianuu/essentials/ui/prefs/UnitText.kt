package com.ivianuu.essentials.ui.prefs

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import kotlin.math.roundToInt

@Composable fun UnitText(value: Any, unit: ValueUnit) {
  Text(stringResource(unit.resId, value))
}

@Composable fun ScaledPercentageUnitText(value: Float) {
  UnitText((value * 100f).roundToInt(), ValueUnit.PERCENTAGE)
}

enum class ValueUnit(val resId: Int) {
  DP(R.string.es_unit_dp),
  MILLIS(R.string.es_unit_millis),
  PERCENTAGE(R.string.es_unit_percentage),
  PX(R.string.es_unit_px),
  SECONDS(R.string.es_unit_seconds)
}
