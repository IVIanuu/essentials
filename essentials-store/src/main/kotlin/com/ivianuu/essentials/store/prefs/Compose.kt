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

package com.ivianuu.essentials.store.prefs

import androidx.ui.core.Dp
import androidx.ui.core.IntPx
import androidx.ui.core.Px
import androidx.ui.core.dp
import androidx.ui.core.ipx
import androidx.ui.core.px
import androidx.ui.graphics.Color
import androidx.ui.graphics.ColorAccessor
import com.ivianuu.essentials.store.DiskBox

fun PrefBoxFactory.color(
    name: String,
    defaultValue: Color
) = box(name = name, defaultValue = defaultValue, serializer = ColorSerializer)

private object ColorSerializer : DiskBox.Serializer<Color> {
    override fun deserialize(serialized: String) = ColorAccessor.newColor(serialized.toLong())
    override fun serialize(value: Color) = value.value.toString()
}

fun PrefBoxFactory.px(
    name: String,
    defaultValue: Px = Px.Zero
) = box(name = name, defaultValue = defaultValue, serializer = PxSerializer)

private object PxSerializer : DiskBox.Serializer<Px> {
    override fun deserialize(serialized: String) = serialized.toFloat().px
    override fun serialize(value: Px) = value.value.toString()
}

fun PrefBoxFactory.dp(
    name: String,
    defaultValue: Dp = 0.dp
) = box(name = name, defaultValue = defaultValue, serializer = DpSerializer)

private object DpSerializer : DiskBox.Serializer<Dp> {
    override fun deserialize(serialized: String) = serialized.toFloat().dp
    override fun serialize(value: Dp) = value.value.toString()
}

fun PrefBoxFactory.ipx(
    name: String,
    defaultValue: IntPx = IntPx.Zero
) = box(name = name, defaultValue = defaultValue, serializer = IntPxSerializer)

private object IntPxSerializer : DiskBox.Serializer<IntPx> {
    override fun deserialize(serialized: String) = serialized.toInt().ipx
    override fun serialize(value: IntPx) = value.value.toString()
}
