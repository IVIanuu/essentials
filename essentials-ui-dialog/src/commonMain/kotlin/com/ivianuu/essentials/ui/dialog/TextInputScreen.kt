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
import com.ivianuu.essentials.Strings
import com.ivianuu.essentials.Strings_Cancel
import com.ivianuu.essentials.Strings_Ok
import com.ivianuu.essentials.compose.action
import com.ivianuu.essentials.ui.material.TextButton
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.ui.navigation.Ui
import com.ivianuu.essentials.ui.navigation.pop
import com.ivianuu.injekt.Provide

class TextInputScreen(
  val initial: String = "",
  val label: String = "",
  val keyboardOptions: KeyboardOptions = KeyboardOptions(),
  val title: String? = null,
  val predicate: (String) -> Boolean = { true }
) : DialogScreen<String>

@Provide fun textInputUi(
  navigator: Navigator,
  screen: TextInputScreen,
  strings: Strings
) = Ui<TextInputScreen, Unit> {
  DialogScaffold {
    var currentValue by remember { mutableStateOf(screen.initial) }
    TextInputDialog(
      value = currentValue,
      onValueChange = { currentValue = it },
      label = { Text(screen.label) },
      keyboardOptions = screen.keyboardOptions,
      title = screen.title?.let { { Text(it) } },
      buttons = {
        TextButton(onClick = action { navigator.pop(screen, null) }) {
          Text(strings[Strings_Cancel])
        }

        val currentValueIsOk = remember(currentValue) { screen.predicate(currentValue) }

        TextButton(
          enabled = currentValueIsOk,
          onClick = action { navigator.pop(screen, currentValue) }
        ) { Text(strings[Strings_Ok]) }
      }
    )
  }
}
