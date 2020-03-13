package com.ivianuu.essentials.moshi.android

import androidx.ui.graphics.Color
import androidx.ui.graphics.ColorAccessor
import androidx.ui.graphics.toArgb
import androidx.ui.unit.Dp
import androidx.ui.unit.IntPx
import androidx.ui.unit.Px
import androidx.ui.unit.dp
import androidx.ui.unit.ipx
import androidx.ui.unit.px
import com.ivianuu.injekt.Factory
import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson

@Factory
class ColorJsonAdapter {
    @FromJson
    fun fromJson(value: String) = ColorAccessor.newColor(value.toLong())
    @ToJson
    fun toJson(color: Color) = color.toArgb()
}

@Factory
class PxJsonAdapter {
    @FromJson
    fun fromJson(value: String) = value.toFloat().px
    @ToJson
    fun toJson(value: Px) = value.value.toString()
}

@Factory
class DpJsonAdapter {
    @FromJson
    fun fromJson(value: String) = value.toFloat().dp
    @ToJson
    fun toJson(value: Dp) = value.value.toString()
}

@Factory
class IntPxJsonAdapter {
    @FromJson
    fun fromJson(value: String) = value.toInt().ipx
    @ToJson
    fun toJson(value: IntPx) = value.value.toString()
}

