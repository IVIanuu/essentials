/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.dialog

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.ivianuu.essentials.ui.common.CommonStrings
import com.ivianuu.essentials.ui.material.TextButton
import com.ivianuu.essentials.ui.navigation.KeyUi
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.injekt.Provide
import kotlinx.coroutines.launch

data class TextInputKey(
  val initial: String = "",
  val label: String,
  val keyboardOptions: KeyboardOptions = KeyboardOptions(),
  val title: String? = null,
  val allowEmpty: Boolean = true,
) : DialogKey<String>

@Provide fun textInputUi(
  key: TextInputKey,
  navigator: Navigator,
  strings: CommonStrings
) = KeyUi<TextInputKey> {
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

        TextButton(
          enabled = key.allowEmpty || currentValue.isNotEmpty(),
          onClick = {
            scope.launch { navigator.pop(key, currentValue) }
          }
        ) { Text(strings.ok) }
      }
    )
  }
}
