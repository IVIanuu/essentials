/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.dialog

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.focus.*
import androidx.compose.ui.text.*
import androidx.compose.ui.text.input.*

@Composable fun TextInputDialog(
  modifier: Modifier = Modifier,
  value: String,
  onValueChange: (String) -> Unit,
  label: @Composable (() -> Unit)? = null,
  icon: @Composable (() -> Unit)? = null,
  title: @Composable (() -> Unit)? = null,
  buttons: @Composable (() -> Unit)? = null,
  keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
) {
  Dialog(
    modifier = modifier,
    icon = icon,
    title = title,
    content = {
      Box {
        val focusRequester = remember { FocusRequester() }
        var textFieldValue by remember {
          mutableStateOf(TextFieldValue(value, TextRange(value.length)))
        }
        textFieldValue = textFieldValue.copy(value)
        TextField(
          modifier = Modifier.focusRequester(focusRequester),
          value = textFieldValue,
          onValueChange = {
            textFieldValue = it
            onValueChange(it.text)
          },
          keyboardOptions = keyboardOptions,
          textStyle = MaterialTheme.typography.subtitle1,
          label = label ?: {}
        )

        DisposableEffect(true) {
          focusRequester.requestFocus()
          onDispose { }
        }
      }
    },
    buttons = buttons
  )
}
