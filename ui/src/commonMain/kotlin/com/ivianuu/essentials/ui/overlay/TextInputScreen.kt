/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.overlay

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.text.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.focus.*
import androidx.compose.ui.text.*
import androidx.compose.ui.text.input.*
import com.ivianuu.essentials.compose.*
import com.ivianuu.essentials.ui.material.EsModalBottomSheet
import com.ivianuu.essentials.ui.navigation.*
import injekt.*

class TextInputScreen(
  val label: String,
  val initial: String = "",
  val keyboardOptions: KeyboardOptions = KeyboardOptions(),
  val predicate: (String) -> Boolean = { true }
) : OverlayScreen<String> {
  @Provide companion object {
    @Provide fun ui(
      navigator: Navigator,
      screen: TextInputScreen
    ) = Ui<TextInputScreen> {
      EsModalBottomSheet {
        var currentValue by remember {
          mutableStateOf(TextFieldValue(screen.initial, TextRange(screen.initial.length)))
        }

        val focusRequester = remember { FocusRequester() }
        DisposableEffect(true) {
          focusRequester.requestFocus()
          onDispose { }
        }

        Row(
          verticalAlignment = Alignment.CenterVertically
        ) {
          TextField(
            modifier = Modifier.focusRequester(focusRequester).weight(1f),
            value = currentValue,
            onValueChange = { currentValue = it },
            keyboardOptions = screen.keyboardOptions,
            textStyle = MaterialTheme.typography.titleLarge,
            label = { Text(screen.label) }
          )

          val currentValueIsOk = remember(currentValue) { screen.predicate(currentValue.text) }
          IconButton(
            onClick = scopedAction { navigator.pop(screen, currentValue.text) },
            enabled = currentValueIsOk
          ) { Icon(Icons.Default.Send, null) }
        }
      }
    }
  }
}
