/*
 * Copyright 2019 Manuel Wrage
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

package com.ivianuu.essentials.ui.core

import androidx.compose.Composable
import androidx.ui.core.Modifier
import androidx.ui.core.currentTextStyle
import androidx.ui.input.ImeAction
import androidx.ui.input.KeyboardType
import androidx.ui.input.VisualTransformation
import androidx.ui.text.TextFieldValue
import androidx.ui.text.TextRange
import androidx.ui.text.TextStyle

// todo remove once supported by original

@Composable
fun TextField(
    value: String,
    modifier: Modifier = Modifier.None,
    onValueChange: (String) -> Unit = {},
    textStyle: TextStyle = currentTextStyle(),
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Unspecified,
    onFocus: () -> Unit = {},
    onBlur: () -> Unit = {},
    focusIdentifier: String? = null,
    onImeActionPerformed: (ImeAction) -> Unit = {},
    visualTransformation: VisualTransformation? = null
) {
    androidx.ui.core.TextField(
        value = value,
        modifier = modifier,
        onValueChange = onValueChange,
        textStyle = ensureColor(textStyle),
        keyboardType = keyboardType,
        imeAction = imeAction,
        onFocus = onFocus,
        onBlur = onBlur,
        focusIdentifier = focusIdentifier,
        onImeActionPerformed = onImeActionPerformed,
        visualTransformation = visualTransformation
    )
}

@Composable
fun TextField(
    value: TextFieldValue,
    modifier: Modifier = Modifier.None,
    onValueChange: (TextFieldValue) -> Unit = {},
    textStyle: TextStyle = currentTextStyle(),
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Unspecified,
    onFocus: () -> Unit = {},
    onBlur: () -> Unit = {},
    focusIdentifier: String? = null,
    onImeActionPerformed: (ImeAction) -> Unit = {},
    visualTransformation: VisualTransformation? = null
) {
    androidx.ui.core.TextField(
        value = value,
        modifier = modifier,
        onValueChange = onValueChange,
        textStyle = ensureColor(textStyle),
        keyboardType = keyboardType,
        imeAction = imeAction,
        onFocus = onFocus,
        onBlur = onBlur,
        focusIdentifier = focusIdentifier,
        onImeActionPerformed = onImeActionPerformed,
        visualTransformation = visualTransformation
    )
}

@Composable
fun TextField(
    model: TextFieldValue,
    compositionRange: TextRange?,
    modifier: Modifier = Modifier.None,
    onValueChange: (TextFieldValue, TextRange?) -> Unit = { _, _ -> },
    textStyle: TextStyle = currentTextStyle(),
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Unspecified,
    onFocus: () -> Unit = {},
    onBlur: () -> Unit = {},
    focusIdentifier: String? = null,
    onImeActionPerformed: (ImeAction) -> Unit = {},
    visualTransformation: VisualTransformation? = null
) {
    androidx.ui.core.TextField(
        model = model,
        compositionRange = compositionRange,
        modifier = modifier,
        onValueChange = onValueChange,
        textStyle = ensureColor(textStyle),
        keyboardType = keyboardType,
        imeAction = imeAction,
        onFocus = onFocus,
        onBlur = onBlur,
        focusIdentifier = focusIdentifier,
        onImeActionPerformed = onImeActionPerformed,
        visualTransformation = visualTransformation
    )
}
