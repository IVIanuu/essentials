package com.ivianuu.essentials.moshi.android

import androidx.ui.graphics.Color
import androidx.ui.unit.Dp
import androidx.ui.unit.IntPx
import androidx.ui.unit.dp
import androidx.ui.unit.ipx
import com.ivianuu.essentials.moshi.BindJsonAdapter
import com.ivianuu.injekt.Transient
import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson

@BindJsonAdapter
@Transient
class ColorJsonAdapter {
    @FromJson
    fun fromJson(value: String) = Color(value.toULong())

    @ToJson
    fun toJson(color: Color) = color.value
}

@BindJsonAdapter
@Transient
class DpJsonAdapter {
    @FromJson
    fun fromJson(value: String) = value.toFloat().dp
    @ToJson
    fun toJson(value: Dp) = value.value.toString()
}

@BindJsonAdapter
@Transient
class IntPxJsonAdapter {
    @FromJson
    fun fromJson(value: String) = value.toInt().ipx
    @ToJson
    fun toJson(value: IntPx) = value.value.toString()
}
