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

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.Text
import androidx.compose.runtime.saveable.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.unit.*
import com.ivianuu.essentials.ui.core.*
import com.ivianuu.essentials.ui.dialog.*
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.injekt.*

@Given
val dynamicSystemBarsHomeItem = HomeItem("Dynamic system bars") { DynamicSystemBarsKey }

object DynamicSystemBarsKey : Key<Nothing>

@Given
val dynamicSystemBarsUi: KeyUi<DynamicSystemBarsKey> = {
    Box {
        val colors: List<Color> = rememberSaveable {
            com.ivianuu.essentials.colorpicker.ColorPickerPalette.values()
                .filter { it != com.ivianuu.essentials.colorpicker.ColorPickerPalette.BLACK && it != com.ivianuu.essentials.colorpicker.ColorPickerPalette.WHITE }
                .flatMap { it.colors }
                .shuffled()
        }
        LazyColumn {
            items(colors) { color ->
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

        TopAppBar(
            backgroundColor = Color.Transparent,
            elevation = 0.dp,
            title = { Text("Dynamic system bars") }
        )
    }
}
