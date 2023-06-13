/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.dialog

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.ivianuu.essentials.compose.action
import com.ivianuu.essentials.ui.common.CommonStrings
import com.ivianuu.essentials.ui.material.TextButton
import com.ivianuu.essentials.ui.navigation.KeyUiContext
import com.ivianuu.essentials.ui.navigation.OverlayKey
import com.ivianuu.essentials.ui.navigation.Ui
import com.ivianuu.essentials.ui.navigation.pop
import com.ivianuu.injekt.Provide

class TextInputKey(
  val initial: String = "",
  val label: String = "",
  val keyboardOptions: KeyboardOptions = KeyboardOptions(),
  val title: String? = null,
  val predicate: (String) -> Boolean = { true }
) : OverlayKey<String>

@Provide fun textInputUi(
  ctx: KeyUiContext<TextInputKey>,
  commonStrings: CommonStrings
) = Ui<TextInputKey, Unit> { model ->
  DialogScaffold {
    var currentValue by remember { mutableStateOf(ctx.key.initial) }
    TextInputDialog(
      value = currentValue,
      onValueChange = { currentValue = it },
      label = { Text(ctx.key.label) },
      keyboardOptions = ctx.key.keyboardOptions,
      title = ctx.key.title?.let { { Text(it) } },
      buttons = {
        TextButton(onClick = action { ctx.navigator.pop(ctx.key, null) }) {
          Text(commonStrings.cancel)
        }

        val currentValueIsOk = remember(currentValue) { ctx.key.predicate(currentValue) }

        TextButton(
          enabled = currentValueIsOk,
          onClick = action { ctx.navigator.pop(ctx.key, currentValue) }
        ) { Text(commonStrings.ok) }
      }
    )
  }
}
