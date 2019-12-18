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

import androidx.compose.Composable
import androidx.ui.material.ColorPalette
import androidx.ui.material.Typography
import androidx.ui.material.darkColorPalette
import androidx.ui.material.lightColorPalette
import com.ivianuu.essentials.ui.coroutines.collect
import com.ivianuu.essentials.ui.injekt.inject
import com.ivianuu.essentials.ui.material.EsTheme
import com.ivianuu.essentials.ui.material.SystemBarConfig
import com.ivianuu.essentials.util.darken

@Composable
fun TwilightTheme(
    lightPalette: @Composable() () -> ColorPalette = { lightColorPalette() },
    darkPalette: @Composable() () -> ColorPalette = { darkColorPalette() },
    typography: Typography = Typography(),
    systemBarConfig: SystemBarConfig? = null,
    children: @Composable() () -> Unit
) {
    val helper = inject<TwilightHelper>()
    val isDark = collect(helper.isDark, helper.currentIsDark)
    val colors = if (isDark) darkPalette() else lightPalette()
    EsTheme(
        colors = colors,
        typography = typography,
        systemBarConfig = systemBarConfig ?: SystemBarConfig(
            statusBarColor = colors.primary.darken()
        ),
        children = children
    )
}