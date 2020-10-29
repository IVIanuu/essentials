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

import androidx.compose.animation.animatedFloat
import androidx.compose.animation.core.TweenSpec
import androidx.compose.material.Colors
import androidx.compose.material.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.onCommit
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.ivianuu.essentials.twilight.domain.TwilightStateFlow
import com.ivianuu.essentials.ui.common.EsMaterialTheme
import com.ivianuu.essentials.ui.common.rememberUntrackedState
import com.ivianuu.essentials.ui.material.lerp
import com.ivianuu.injekt.Assisted
import com.ivianuu.injekt.FunBinding
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

@FunBinding
@Composable
fun TwilightTheme(
    twilightStateFlow: TwilightStateFlow,
    lightColors: @Assisted Colors,
    darkColors: @Assisted Colors,
    blackColors: @Assisted Colors,
    typography: @Assisted Typography,
    children: @Assisted @Composable () -> Unit
) {
    val targetColors by twilightStateFlow
        .map {
            if (it.isDark) {
                if (it.useBlack) blackColors else darkColors
            } else lightColors
        }
        .distinctUntilChanged()
        .collectAsState(lightColors)

    var lastColors by rememberUntrackedState { targetColors }

    val animation = key(targetColors) { animatedFloat(0f) }
    onCommit(animation) {
        animation.animateTo(1f, anim = TweenSpec(durationMillis = 150))
    }

    val animatedColors = remember(animation.value) {
        lerp(
            lastColors,
            targetColors,
            animation.value
        )
    }
    lastColors = animatedColors

    EsMaterialTheme(
        colors = animatedColors,
        typography = typography,
        content = children
    )
}
