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

package com.ivianuu.essentials.sample.ui

import androidx.compose.foundation.Text
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.ivianuu.essentials.screenstate.DisplayRotation
import com.ivianuu.essentials.screenstate.DisplayRotationFlow
import com.ivianuu.essentials.ui.core.systemBarStyle
import com.ivianuu.injekt.FunBinding

typealias DisplayRotationPage = @Composable () -> Unit

@FunBinding
@Composable
fun DisplayRotationPage(displayRotationFlow: DisplayRotationFlow) {
    Box(
        modifier = Modifier.fillMaxSize()
            .background(MaterialTheme.colors.primary)
            .systemBarStyle(MaterialTheme.colors.primary),
        alignment = Alignment.Center
    ) {
        val displayRotation by displayRotationFlow.collectAsState(null)
        Text(
            text = when (displayRotation) {
                DisplayRotation.PortraitUp -> "Portrait up"
                DisplayRotation.LandscapeLeft -> "Landscape left"
                DisplayRotation.PortraitDown -> "Portrait down"
                DisplayRotation.LandscapeRight -> "Landscape right"
                null -> "Unknown"
            },
            style = MaterialTheme.typography.h4
        )
    }
}