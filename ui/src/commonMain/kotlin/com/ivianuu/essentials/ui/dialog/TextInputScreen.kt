/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.dialog

import androidx.compose.foundation.text.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.focus.*
import androidx.compose.ui.text.*
import androidx.compose.ui.text.input.*
import com.ivianuu.essentials.compose.*
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.injekt.*

class TextInputScreen(
  val initial: String = "",
  val label: String = "",
  val keyboardOptions: KeyboardOptions = KeyboardOptions(),
  val title: String? = null,
  val predicate: (String) -> Boolean = { true }
) : OverlayScreen<String> {
  @Provide companion object {
    @Provide fun ui(
      navigator: Navigator,
      screen: TextInputScreen
    ) = Ui<TextInputScreen> {
      var currentValue by remember {
        mutableStateOf(TextFieldValue(screen.initial, TextRange(screen.initial.length)))
      }

      AlertDialog(
        onDismissRequest = action { navigator.pop(screen, null) },
        title = screen.title?.let { { Text(it) } },
        text = {
          val focusRequester = remember { FocusRequester() }

          TextField(
            modifier = Modifier.focusRequester(focusRequester),
            value = currentValue,
            onValueChange = { currentValue = it },
            keyboardOptions = screen.keyboardOptions,
            textStyle = MaterialTheme.typography.subtitle1,
            label = { Text(screen.label) }
          )

          DisposableEffect(true) {
            focusRequester.requestFocus()
            onDispose { }
          }
        },
        confirmButton = {
          val currentValueIsOk = remember(currentValue) { screen.predicate(currentValue.text) }

          TextButton(
            enabled = currentValueIsOk,
            onClick = action { navigator.pop(screen, currentValue.text) }
          ) { Text("OK") }
        },
        dismissButton = {
          TextButton(onClick = action { navigator.pop(screen, null) }) {
            Text("Cancel")
          }
        }
      )
    }
  }
}
