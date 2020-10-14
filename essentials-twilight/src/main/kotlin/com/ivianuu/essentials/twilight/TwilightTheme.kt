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

import androidx.compose.animation.animatedFloat
import androidx.compose.animation.core.TweenSpec
import androidx.compose.material.Colors
import androidx.compose.material.Typography
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.onCommit
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import com.ivianuu.essentials.ui.animatedstack.AnimatedBox
import com.ivianuu.essentials.ui.common.EsMaterialTheme
import com.ivianuu.essentials.ui.common.rememberUntrackedState
import com.ivianuu.essentials.ui.material.lerp
import com.ivianuu.injekt.Assisted
import com.ivianuu.injekt.FunBinding

@FunBinding
@Composable
fun TwilightTheme(
    twilightStateFlow: TwilightStateFlow,
    lightColors: @Assisted Colors = lightColors(),
    darkColors: @Assisted Colors = darkColors(),
    blackColors: @Assisted Colors = darkColors.copy(
        background = Color.Black,
        surface = Color.Black
    ),
    typography: @Assisted Typography = Typography(),
    content: @Assisted @Composable () -> Unit,
) {
    val twilightState by twilightStateFlow.collectAsState()
    AnimatedBox(current = twilightState) { currentTwilightState ->
        fun colorsForTwilightState() = if (currentTwilightState.isDark) {
            if (currentTwilightState.useBlack) blackColors else darkColors
        } else lightColors

        val lastColors = rememberUntrackedState { colorsForTwilightState() }
        val targetColors = colorsForTwilightState()

        val animation = key(currentTwilightState) { animatedFloat(0f) }
        onCommit(animation) {
            animation.animateTo(1f, anim = TweenSpec(durationMillis = 150))
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
