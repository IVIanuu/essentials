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

import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.savedinstancestate.rememberSavedInstanceState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.unit.dp
import com.ivianuu.essentials.ui.core.isLight
import com.ivianuu.essentials.ui.core.systemBarStyle
import com.ivianuu.essentials.ui.dialog.ColorPickerPalette
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.KeyUiBinding
import com.ivianuu.injekt.FunBinding

@HomeItemBinding("Dynamic system bars")
class DynamicSystemBarsKey

@KeyUiBinding<DynamicSystemBarsKey>
@FunBinding
@Composable
fun DynamicSystemBarsScreen() {
    Box {
        ScrollableColumn {
            val colors: List<Color> = rememberSavedInstanceState {
                ColorPickerPalette.values()
                    .filter { it != ColorPickerPalette.Black && it != ColorPickerPalette.White }
                    .flatMap { it.colors }
                    .shuffled()
            }

            colors.forEach { color ->
                key(color) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                            .background(color)
                            .systemBarStyle(
                                bgColor = Color.Black.copy(alpha = 0.2f),
                                lightIcons = color.isLight
                            )
                    )
                }
            }
        }

        TopAppBar(
            backgroundColor = Color.Transparent,
            elevation = 0.dp,
            title = { Text("Dynamic system bars") }
        )
    }
}
