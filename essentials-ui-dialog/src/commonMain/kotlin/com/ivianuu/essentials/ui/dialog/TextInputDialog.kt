/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.dialog

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue

@Composable fun TextInputDialog(
  modifier: Modifier = Modifier,
  value: String,
  onValueChange: (String) -> Unit,
  label: (@Composable () -> Unit)? = null,
  icon: (@Composable () -> Unit)? = null,
  title: (@Composable () -> Unit)? = null,
  buttons: (@Composable () -> Unit)? = null,
  keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
) {
  Dialog(
    modifier = modifier,
    icon = icon,
    title = title,
    content = {
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
    },
    buttons = buttons
  )
}
