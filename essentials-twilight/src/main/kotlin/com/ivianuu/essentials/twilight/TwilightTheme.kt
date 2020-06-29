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

package com.ivianuu.essentials.twilight

import androidx.animation.TweenBuilder
import androidx.compose.Composable
import androidx.compose.key
import androidx.compose.onCommit
import androidx.compose.remember
import androidx.ui.animation.animatedFloat
import androidx.ui.graphics.Color
import androidx.ui.material.ColorPalette
import androidx.ui.material.Typography
import androidx.ui.material.darkColorPalette
import androidx.ui.material.lightColorPalette
import com.ivianuu.essentials.ui.common.EsMaterialTheme
import com.ivianuu.essentials.ui.common.untrackedState
import com.ivianuu.essentials.ui.material.copy
import com.ivianuu.essentials.ui.material.lerp
import com.ivianuu.essentials.ui.resource.ResourceBox
import com.ivianuu.essentials.ui.resource.collectAsResource
import com.ivianuu.injekt.Transient

@Transient
class TwilightTheme(private val helper: TwilightHelper) {

    @Composable
    operator fun invoke(
        lightColors: ColorPalette = lightColorPalette(),
        darkColors: ColorPalette = darkColorPalette(),
        blackColors: ColorPalette = darkColors.copy(
            background = Color.Black,
            surface = Color.Black
        ),
        typography: Typography = Typography(),
        content: @Composable () -> Unit
    ) {
        ResourceBox(resource = helper.state.collectAsResource()) { twilightState ->
            fun colorsForTwilightState() = if (twilightState.isDark) {
                if (twilightState.useBlack) blackColors else darkColors
            } else lightColors

            val lastColors = untrackedState { colorsForTwilightState() }
            val targetColors = colorsForTwilightState()

            val animation = key(twilightState) { animatedFloat(0f) }
            onCommit(animation) {
                animation.animateTo(1f, anim = TweenBuilder<Float>().apply {
                    duration = 150
                })
            }

            val currentColors = remember(animation.value) {
                lerp(
                    lastColors.value,
                    targetColors,
                    animation.value
                )
            }
            lastColors.value = currentColors

            EsMaterialTheme(
                colors = currentColors,
                typography = typography,
                content = content
            )
        }
    }

}
