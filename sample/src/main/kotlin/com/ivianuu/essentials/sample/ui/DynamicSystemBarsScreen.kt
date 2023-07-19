/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
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
import com.ivianuu.essentials.ui.common.VerticalList
import com.ivianuu.essentials.ui.dialog.ColorPickerPalette
import com.ivianuu.essentials.ui.material.AppBar
import com.ivianuu.essentials.ui.navigation.Screen
import com.ivianuu.essentials.ui.navigation.Ui
import com.ivianuu.essentials.ui.systembars.systemBarStyle
import com.ivianuu.essentials.ui.util.isLight
import com.ivianuu.injekt.Provide

@Provide val dynamicSystemBarsHomeItem = HomeItem("Dynamic system bars") { DynamicSystemBarsScreen() }

class DynamicSystemBarsScreen : Screen<Unit>

@Provide val dynamicSystemBarsUi = Ui<DynamicSystemBarsScreen, Unit> {
  Box {
    val colors = rememberSaveable {
      ColorPickerPalette.entries
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
                darkIcons = color.isLight
              )
          )
        }
      }
    }

    AppBar(
      backgroundColor = Color.Transparent,
      elevation = 0.dp,
      title = { Text("Dynamic system bars") }
    )
  }
}
