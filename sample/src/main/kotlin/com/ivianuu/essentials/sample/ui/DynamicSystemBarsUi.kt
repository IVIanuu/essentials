/*
 * Copyright 2021 Manuel Wrage
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

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.Text
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ivianuu.essentials.colorpicker.ColorPickerPalette
import com.ivianuu.essentials.ui.common.VerticalList
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.KeyUi
import com.ivianuu.essentials.ui.systembars.systemBarStyle
import com.ivianuu.essentials.ui.util.isLight
import com.ivianuu.injekt.Provide

@Provide val dynamicSystemBarsHomeItem = HomeItem("Dynamic system bars") { DynamicSystemBarsKey }

object DynamicSystemBarsKey : Key<Unit>

@Provide val dynamicSystemBarsUi = KeyUi<DynamicSystemBarsKey> {
  Box {
    val colors = rememberSaveable {
      ColorPickerPalette.values()
        .filter { it != ColorPickerPalette.BLACK && it != ColorPickerPalette.WHITE }
        .flatMap { it.colors }
        .shuffled()
    }
    VerticalList(contentPadding = PaddingValues(0.dp)) {
      // todo use items once fixed
      for (color in colors) {
        item {
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
