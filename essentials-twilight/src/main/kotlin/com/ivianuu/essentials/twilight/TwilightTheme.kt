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

import androidx.animation.FloatPropKey
import androidx.animation.transitionDefinition
import androidx.compose.Composable
import androidx.ui.animation.Transition
import androidx.ui.material.ColorPalette
import androidx.ui.material.Typography
import androidx.ui.material.darkColorPalette
import androidx.ui.material.lightColorPalette
import com.ivianuu.essentials.android.ui.core.EsTheme
import com.ivianuu.essentials.android.ui.coroutines.collect
import com.ivianuu.essentials.android.ui.injekt.inject
import com.ivianuu.essentials.android.ui.material.lerp

@Composable
fun TwilightTheme(
    lightColors: ColorPalette = lightColorPalette(),
    darkColors: ColorPalette = darkColorPalette(),
    typography: Typography = Typography(),
    children: @Composable () -> Unit
) {
    val helper = inject<TwilightHelper>()
    val isDark = collect(helper.isDark, helper.currentIsDark)

    Transition(
        definition = TwilightTransitionDefinition,
        toState = isDark
    ) { state ->
        val colors = lerp(lightColors, darkColors, state[Fraction])
        EsTheme(
            colors = colors,
            typography = typography,
            children = children
        )
    }
}

private val Fraction = FloatPropKey()
private val TwilightTransitionDefinition = transitionDefinition {
    state(true) { set(Fraction, 1f) }
    state(false) { set(Fraction, 0f) }

    transition {
        Fraction using tween {
            duration = 150
        }
    }
}
