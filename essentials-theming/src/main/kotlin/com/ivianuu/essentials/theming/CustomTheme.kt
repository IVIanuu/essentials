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

package com.ivianuu.essentials.theming

import androidx.animation.TweenBuilder
import androidx.compose.Composable
import androidx.compose.Immutable
import androidx.compose.remember
import androidx.ui.animation.animatedFloat
import androidx.ui.material.ColorPalette
import androidx.ui.material.Typography
import com.ivianuu.essentials.ui.common.ref
import com.ivianuu.essentials.ui.coroutines.collect
import com.ivianuu.essentials.ui.injekt.inject
import com.ivianuu.essentials.ui.material.EsTheme
import com.ivianuu.essentials.ui.material.lerp

@Composable
fun CustomTheme(
    typography: Typography = Typography(),
    children: @Composable() () -> Unit
) {
    val helper = inject<ThemingHelper>()
    val state = collect(remember { helper.state }, helper.currentState)

    RenderTheme(
        colors = state.colors,
        typography = remember(typography) { TypographyWrapper(typography) },
        children = children
    )
}

@Immutable
private data class TypographyWrapper(val typography: Typography)

@Composable
private fun RenderTheme(
    colors: ColorPalette,
    typography: TypographyWrapper,
    children: @Composable() () -> Unit
) {
    val animation = animatedFloat(0f)
    val lastColors = ref { colors }
    val currentColors = ref { colors }

    if (colors != currentColors.value) {
        lastColors.value = currentColors.value
        currentColors.value = colors
        animation.snapTo(0f)
        animation.animateTo(
            targetValue = 1f,
            anim = TweenBuilder<Float>().apply {
                duration = 300
            }
        )
    }

    EsTheme(
        colors = remember(lastColors.value, currentColors.value, animation.value) {
            lerp(lastColors.value, currentColors.value, animation.value)
        },
        typography = typography.typography,
        children = children
    )
}