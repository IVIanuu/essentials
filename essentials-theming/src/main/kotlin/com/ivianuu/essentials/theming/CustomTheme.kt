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

import androidx.compose.Composable
import androidx.compose.remember
import androidx.ui.graphics.Color
import androidx.ui.material.Typography
import com.ivianuu.essentials.ui.box.unfoldBox
import com.ivianuu.essentials.ui.coroutines.collect
import com.ivianuu.essentials.ui.injekt.inject
import com.ivianuu.essentials.ui.material.ColorPalette
import com.ivianuu.essentials.ui.material.EsTheme

@Composable
fun CustomTheme(
    typography: Typography = Typography(),
    children: @Composable() () -> Unit
) {
    val prefs = inject<ThemePrefs>()

    val primaryColor = unfoldBox(prefs.primaryColor).value
    val secondaryColor = unfoldBox(prefs.secondaryColor).value
    val useBlack = unfoldBox(prefs.useBlack).value
    val helper = inject<TwilightHelper>()
    val isDark = collect(remember { helper.isDark }, helper.currentIsDark)

    val backgroundColor = remember(isDark, useBlack) {
        if (!isDark) Color.White else if (useBlack) Color.Black else Color(0xFF121212)
    }

    val colorPalette = ColorPalette(
        isLight = !isDark,
        primary = primaryColor,
        secondary = secondaryColor,
        background = backgroundColor,
        surface = backgroundColor
    )

    EsTheme(
        colors = colorPalette,
        typography = typography,
        children = children
    )

    /*Transition(
        definition = twilightTransitionDefinition,
        toState = isDark
    ) { state ->
        val colors = lerp(lightColors, darkColors, state[Fraction])
        EsTheme(
            colors = colors,
            typography = typography,
            systemBarConfig = SystemBarStyle(
                statusBarColor = colors.primary.darken()
            ),
            children = children
        )
    }*/
}

/*
private val Fraction = FloatPropKey()
private val twilightTransitionDefinition = transitionDefinition {
    state(true) { set(Fraction, 1f) }
    state(false) { set(Fraction, 0f) }

    transition {
        Fraction using tween {
            duration = 300
        }
    }
}
*/