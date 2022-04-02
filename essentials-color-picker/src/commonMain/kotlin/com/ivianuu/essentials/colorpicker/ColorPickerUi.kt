/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.colorpicker

import androidx.compose.material.Text
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.Color
import com.ivianuu.essentials.ui.dialog.DialogScaffold
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.ui.navigation.PopupKey
import com.ivianuu.essentials.ui.navigation.SimpleKeyUi
import com.ivianuu.essentials.ui.navigation.pop
import com.ivianuu.injekt.Provide
import kotlinx.coroutines.launch

data class ColorPickerKey(
  val initialColor: Color,
  val colorPalettes: List<ColorPickerPalette> = ColorPickerPalette.values().toList(),
  val title: String? = null,
  val allowCustomArgb: Boolean = true,
  val showAlphaSelector: Boolean = false,
) : PopupKey<Color>

@Provide fun colorPickerUi(
  key: ColorPickerKey,
  navigator: Navigator,
) = SimpleKeyUi<ColorPickerKey> {
  DialogScaffold {
    val scope = rememberCoroutineScope()
    ColorPickerDialog(
      initialColor = key.initialColor,
      onColorSelected = {
        scope.launch { navigator.pop(key, it) }
      },
      onCancel = {
        scope.launch { navigator.pop(key, null) }
      },
      colorPalettes = key.colorPalettes,
      showAlphaSelector = key.showAlphaSelector,
      allowCustomArgb = key.allowCustomArgb,
      title = key.title?.let { { Text(it) } }
    )
  }
}
