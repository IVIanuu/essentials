/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.dialog

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.ivianuu.essentials.compose.action
import com.ivianuu.essentials.compose.getValue
import com.ivianuu.essentials.compose.setValue
import com.ivianuu.essentials.ui.common.CommonStrings
import com.ivianuu.essentials.ui.material.TextButton
import com.ivianuu.essentials.ui.navigation.KeyUiContext
import com.ivianuu.essentials.ui.navigation.PopupKey
import com.ivianuu.essentials.ui.navigation.SimpleKeyUi
import com.ivianuu.essentials.ui.navigation.pop
import com.ivianuu.injekt.Provide

data class TextInputKey(
  val initial: String = "",
  val label: String = "",
  val keyboardOptions: KeyboardOptions = KeyboardOptions(),
  val title: String? = null,
  val predicate: (String) -> Boolean = { true }
) : PopupKey<String>

context(KeyUiContext<TextInputKey>, CommonStrings)
    @Provide fun textInputUi() = SimpleKeyUi<TextInputKey> {
  DialogScaffold {
    var currentValue by remember { mutableStateOf(key.initial) }
    TextInputDialog(
      value = currentValue,
      onValueChange = { currentValue = it },
      label = { Text(key.label) },
      keyboardOptions = key.keyboardOptions,
      title = key.title?.let { { Text(it) } },
      buttons = {
        TextButton(onClick = action { navigator.pop(key, null) }) {
          Text(cancel)
        }

        val currentValueIsOk = remember(currentValue) { key.predicate(currentValue) }

        TextButton(
          enabled = currentValueIsOk,
          onClick = action { navigator.pop(key, currentValue) }
        ) { Text(ok) }
      }
    )
  }
}
