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

import androidx.compose.foundation.text.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.res.*
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.injekt.*
import kotlinx.coroutines.*

data class TextInputKey(
  val initial: String = "",
  val label: String,
  val keyboardOptions: KeyboardOptions = KeyboardOptions(),
  val title: String? = null,
  val allowEmpty: Boolean = true,
) : DialogKey<String>

@Provide fun textInputUi(
  key: TextInputKey,
  navigator: Navigator
): KeyUi<TextInputKey> = {
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
          Text(R.string.es_cancel)
        }

        TextButton(
          enabled = key.allowEmpty || currentValue.isNotEmpty(),
          onClick = {
            scope.launch { navigator.pop(key, currentValue) }
          }
        ) { Text(R.string.es_ok) }
      }
    )
  }
}
