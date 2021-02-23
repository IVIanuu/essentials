/*
 * Copyright 2020 Manuel Wrage
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

package com.ivianuu.essentials.twilight.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.TweenSpec
import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Typography
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import com.ivianuu.essentials.twilight.domain.TwilightState
import com.ivianuu.essentials.ui.common.getValue
import com.ivianuu.essentials.ui.common.refOf
import com.ivianuu.essentials.ui.common.setValue
import com.ivianuu.essentials.ui.material.blackColors
import com.ivianuu.essentials.ui.material.lerp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

@Composable
fun TwilightTheme(
    lightColors: Colors = lightColors(),
    darkColors: Colors = darkColors(),
    blackColors: Colors = blackColors(),
    typography: Typography = Typography(),
    twilightState: Flow<TwilightState>,
    content: @Composable () -> Unit
) {
    val targetColors by remember(twilightState) {
        twilightState
            .map {
                if (it.isDark) {
                    if (it.useBlack) blackColors else darkColors
                } else lightColors
            }
            .distinctUntilChanged()
    }.collectAsState(lightColors)

    var lastColors by remember { refOf(targetColors) }

    val animation = key(targetColors) { remember { Animatable(0f) } }
    LaunchedEffect(animation) {
        animation.animateTo(1f, animationSpec = TweenSpec(durationMillis = 150))
    }

    val animatedColors = remember(animation.value) {
        lerp(
            lastColors,
            targetColors,
            animation.value
        )
    }
    lastColors = animatedColors

    MaterialTheme(
        colors = animatedColors,
        typography = typography,
        content = content
    )
}
