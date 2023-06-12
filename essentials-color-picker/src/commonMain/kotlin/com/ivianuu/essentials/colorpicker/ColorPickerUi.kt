/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.colorpicker

import androidx.compose.material.Text
import androidx.compose.ui.graphics.Color
import com.ivianuu.essentials.compose.action
import com.ivianuu.essentials.ui.dialog.DialogScaffold
import com.ivianuu.essentials.ui.navigation.KeyUi
import com.ivianuu.essentials.ui.navigation.KeyUiContext
import com.ivianuu.essentials.ui.navigation.OverlayKey
import com.ivianuu.essentials.ui.navigation.pop
import com.ivianuu.injekt.Provide

class ColorPickerKey(
  val initialColor: Color,
  val colorPalettes: List<ColorPickerPalette> = ColorPickerPalette.values().toList(),
  val title: String? = null,
  val allowCustomArgb: Boolean = true,
  val showAlphaSelector: Boolean = false,
) : OverlayKey<Color>

@Provide fun colorPickerUi(ctx: KeyUiContext<ColorPickerKey>) =
  KeyUi<ColorPickerKey, Unit> { model ->
    DialogScaffold {
      ColorPickerDialog(
        initialColor = ctx.key.initialColor,
        onColorSelected = action { color -> ctx.navigator.pop(ctx.key, color) },
        onCancel = action { ctx.navigator.pop(ctx.key, null) },
        colorPalettes = ctx.key.colorPalettes,
        showAlphaSelector = ctx.key.showAlphaSelector,
        allowCustomArgb = ctx.key.allowCustomArgb,
        title = ctx.key.title?.let { { Text(it) } }
      )
  }
}
