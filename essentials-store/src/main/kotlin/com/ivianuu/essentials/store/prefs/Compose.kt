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
import com.ivianuu.essentials.store.map

fun PrefBoxFactory.color(
    name: String,
    defaultValue: Color
) = long(name, defaultValue.value)
    .map(
        fromRaw = { Color(it) },
        toRaw = { it.value }
    )

fun PrefBoxFactory.px(
    name: String,
    defaultValue: Px = Px.Zero
) = float(name, defaultValue.value)
    .map(
        fromRaw = { it.px },
        toRaw = { it.value }
    )

fun PrefBoxFactory.dp(
    name: String,
    defaultValue: Dp = 0.dp
) = float(name, defaultValue.value)
    .map(
        fromRaw = { it.dp },
        toRaw = { it.value }
    )

fun PrefBoxFactory.ipx(
    name: String,
    defaultValue: IntPx = IntPx.Zero
) = int(name, defaultValue.value)
    .map(
        fromRaw = { it.ipx },
        toRaw = { it.value }
    )