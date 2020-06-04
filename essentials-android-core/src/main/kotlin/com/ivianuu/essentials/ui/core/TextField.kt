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
import androidx.ui.foundation.TextFieldValue
import androidx.ui.foundation.currentTextStyle
import androidx.ui.input.ImeAction
import androidx.ui.input.KeyboardType
import androidx.ui.input.VisualTransformation
import androidx.ui.text.TextLayoutResult
import androidx.ui.text.TextStyle

@Composable
fun TextField(
    value: TextFieldValue,
    modifier: Modifier = Modifier,
    onValueChange: (TextFieldValue) -> Unit,
    textStyle: TextStyle = currentTextStyle(),
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Unspecified,
    onFocusChange: (Boolean) -> Unit = {},
    onImeActionPerformed: (ImeAction) -> Unit = {},
    onTextLayout: (TextLayoutResult) -> Unit = {},
    visualTransformation: VisualTransformation = VisualTransformation.None
) {
    androidx.ui.foundation.TextField(
        value = value,
        modifier = modifier,
        onValueChange = onValueChange,
        textStyle = ensureColor(textStyle),
        keyboardType = keyboardType,
        imeAction = imeAction,
        onFocusChange = onFocusChange,
        onImeActionPerformed = onImeActionPerformed,
        visualTransformation = visualTransformation,
        onTextLayout = onTextLayout
    )
}
