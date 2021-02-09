/*
 * Copyright 2020 Manuel Wrage
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

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.ivianuu.essentials.ui.core.Text
import com.ivianuu.essentials.ui.core.rememberState
import com.ivianuu.essentials.ui.navigation.KeyUiBinding
import com.ivianuu.essentials.ui.navigation.NavigationOptionFactoryBinding
import com.ivianuu.essentials.ui.navigation.popTopKeyWithResult
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.GivenFun

data class TextInputKey(
    val initial: String = "",
    val label: String,
    val keyboardOptions: KeyboardOptions = KeyboardOptions(),
    val title: String? = null,
    val allowEmpty: Boolean = true,
)

typealias TextInputResult = String

@KeyUiBinding<TextInputKey>
@GivenFun
@Composable
fun TextInputDialog(
    @Given key: TextInputKey,
    @Given popTopKeyWithResult: popTopKeyWithResult<TextInputResult>,
) {
    DialogWrapper {
        var currentValue by rememberState { key.initial }
        TextInputDialog(
            value = currentValue,
            onValueChange = { currentValue = it },
            label = { Text(key.label) },
            keyboardOptions = key.keyboardOptions,
            title = key.title?.let { { Text(key.title) } },
            positiveButton = {
                TextButton(
                    enabled = key.allowEmpty || currentValue.isNotEmpty(),
                    onClick = { popTopKeyWithResult(currentValue) }
                ) { Text(R.string.es_ok) }
            },
            negativeButton = {
                TextButton(onClick = { popTopKeyWithResult(null) }) {
                    Text(R.string.es_cancel)
                }
            }
        )
    }
}

@NavigationOptionFactoryBinding
@Given
val textInputDialogNavigationOptionsFactory = DialogNavigationOptionsFactory<TextInputKey>()
