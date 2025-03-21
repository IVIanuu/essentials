/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.ui.overlay

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.focus.*
import androidx.compose.ui.text.*
import androidx.compose.ui.text.input.*
import essentials.compose.*
import essentials.ui.material.*
import essentials.ui.navigation.*
import injekt.*

class TextInputScreen(
  val label: String,
  val initial: String = "",
  val keyboardOptions: KeyboardOptions = KeyboardOptions(),
  val predicate: (String) -> Boolean = { true }
) : OverlayScreen<String>

@Provide @Composable fun TextInputUi(
  context: ScreenContext<TextInputScreen> = inject
): Ui<TextInputScreen> {
  BottomSheet {
    var currentValue by remember {
      mutableStateOf(TextFieldValue(context.screen.initial, TextRange(context.screen.initial.length)))
    }

    val focusRequester = remember { FocusRequester() }
    DisposableEffect(true) {
      focusRequester.requestFocus()
      onDispose { }
    }

    Row(verticalAlignment = Alignment.CenterVertically) {
      TextField(
        modifier = Modifier.focusRequester(focusRequester).weight(1f),
        value = currentValue,
        onValueChange = { currentValue = it },
        keyboardOptions = context.screen.keyboardOptions,
        textStyle = MaterialTheme.typography.titleLarge,
        label = { Text(context.screen.label) }
      )

      val currentValueIsOk = remember(currentValue) {
        context.screen.predicate(currentValue.text)
      }
      IconButton(
        onClick = scopedAction { popWithResult(currentValue.text) },
        enabled = currentValueIsOk
      ) { Icon(Icons.Default.Send, null) }
    }
  }
}
