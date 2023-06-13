/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.colorpicker

import androidx.compose.material.Text
import androidx.compose.ui.graphics.Color
import com.ivianuu.essentials.compose.action
import com.ivianuu.essentials.ui.dialog.DialogScaffold
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.ui.navigation.OverlayKey
import com.ivianuu.essentials.ui.navigation.Ui
import com.ivianuu.essentials.ui.navigation.pop
import com.ivianuu.injekt.Provide

class ColorPickerKey(
  val initialColor: Color,
  val colorPalettes: List<ColorPickerPalette> = ColorPickerPalette.values().toList(),
  val title: String? = null,
  val allowCustomArgb: Boolean = true,
  val showAlphaSelector: Boolean = false,
) : OverlayKey<Color>

@Provide fun colorPickerUi(
  key: ColorPickerKey,
  navigator: Navigator
) = Ui<ColorPickerKey, Unit> {
  DialogScaffold {
    ColorPickerDialog(
      initialColor = key.initialColor,
      onColorSelected = action { color -> navigator.pop(key, color) },
      onCancel = action { navigator.pop(key, null) },
      colorPalettes = key.colorPalettes,
      showAlphaSelector = key.showAlphaSelector,
      allowCustomArgb = key.allowCustomArgb,
      title = key.title?.let { { Text(it) } }
    )
  }
}
