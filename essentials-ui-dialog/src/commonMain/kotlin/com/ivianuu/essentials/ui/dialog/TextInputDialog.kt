/*
 * Copyright 2021 Manuel Wrage
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ivianuu.essentials.ui.dialog

import androidx.compose.foundation.layout.Box
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
