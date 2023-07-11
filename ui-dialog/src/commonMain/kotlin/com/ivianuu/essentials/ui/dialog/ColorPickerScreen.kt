/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.dialog

import androidx.compose.material.Text
import androidx.compose.ui.graphics.Color
import com.ivianuu.essentials.compose.action
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.ui.navigation.Ui
import com.ivianuu.essentials.ui.navigation.pop
import com.ivianuu.injekt.Provide

class ColorPickerScreen(
  val initialColor: Color,
  val colorPalettes: List<ColorPickerPalette> = ColorPickerPalette.entries,
  val title: String? = null,
  val allowCustomArgb: Boolean = true,
  val showAlphaSelector: Boolean = false,
) : DialogScreen<Color>

@Provide fun colorPickerUi(
  navigator: Navigator,
  screen: ColorPickerScreen
) = Ui<ColorPickerScreen, Unit> {
  DialogScaffold {
    ColorPickerDialog(
      initialColor = screen.initialColor,
      onColorSelected = action { color -> navigator.pop(screen, color) },
      onCancel = action { navigator.pop(screen, null) },
      colorPalettes = screen.colorPalettes,
      showAlphaSelector = screen.showAlphaSelector,
      allowCustomArgb = screen.allowCustomArgb,
      title = screen.title?.let { { Text(it) } }
    )
  }
}
