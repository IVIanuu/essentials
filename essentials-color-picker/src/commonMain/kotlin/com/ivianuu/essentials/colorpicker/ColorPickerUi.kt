/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.colorpicker

import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.*
import com.ivianuu.essentials.ui.dialog.*
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.injekt.*
import kotlinx.coroutines.*

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
) = KeyUi<ColorPickerKey> {
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
