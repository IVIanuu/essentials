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

package com.ivianuu.essentials.ui.compose.material

import androidx.compose.Composable
import androidx.compose.effectOf
import androidx.ui.graphics.Color
import androidx.ui.material.ripple.CurrentRippleTheme
import androidx.ui.material.ripple.DefaultRippleEffectFactory
import androidx.ui.material.ripple.RippleTheme
import com.ivianuu.essentials.ui.compose.core.composable
import com.ivianuu.essentials.ui.compose.core.memo

@Composable
fun RippleColorProvider(
    color: Color,
    children: @Composable() () -> Unit
) = composable("RippleColor") {
    val theme = memo(color) {
        RippleTheme(
            factory = DefaultRippleEffectFactory,
            defaultColor = effectOf { color },
            opacity = effectOf { color.alpha }
        )
    }
    CurrentRippleTheme.Provider(value = theme, children = children)
}