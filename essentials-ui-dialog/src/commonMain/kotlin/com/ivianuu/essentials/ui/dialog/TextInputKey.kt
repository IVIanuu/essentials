/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.dialog

import androidx.compose.foundation.text.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import com.ivianuu.essentials.ui.common.*
import com.ivianuu.essentials.ui.material.TextButton
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.injekt.*
import kotlinx.coroutines.*

data class TextInputKey(
  val initial: String = "",
  val label: String,
  val keyboardOptions: KeyboardOptions = KeyboardOptions(),
  val title: String? = null,
  val predicate: (String) -> Boolean = { true }
) : PopupKey<String>

@Provide fun textInputUi(
  key: TextInputKey,
  navigator: Navigator,
  strings: CommonStrings
) = SimpleKeyUi<TextInputKey> {
  DialogScaffold {
    var currentValue by remember { mutableStateOf(key.initial) }
    TextInputDialog(
      value = currentValue,
      onValueChange = { currentValue = it },
      label = { Text(key.label) },
      keyboardOptions = key.keyboardOptions,
      title = key.title?.let { { Text(key.title) } },
      buttons = {
        val scope = rememberCoroutineScope()
        TextButton(onClick = {
          scope.launch { navigator.pop(key, null) }
        }) {
          Text(strings.cancel)
        }

        val currentValueIsOk = remember(currentValue) { key.predicate(currentValue) }

        TextButton(
          enabled = currentValueIsOk,
          onClick = {
            scope.launch { navigator.pop(key, currentValue) }
          }
        ) { Text(strings.ok) }
      }
    )
  }
}
