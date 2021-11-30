/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.sample.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.saveable.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.unit.*
import com.ivianuu.essentials.colorpicker.*
import com.ivianuu.essentials.ui.common.*
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.essentials.ui.systembars.*
import com.ivianuu.essentials.ui.util.*
import com.ivianuu.injekt.*

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
