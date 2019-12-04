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

package com.ivianuu.essentials.ui.compose.dialog

import androidx.compose.Composable
import androidx.ui.core.Opacity
import androidx.ui.material.Button
import androidx.ui.material.ButtonStyle
import androidx.ui.material.TextButtonStyle
import com.ivianuu.essentials.ui.compose.core.composable
import com.ivianuu.essentials.ui.compose.injekt.inject
import com.ivianuu.essentials.ui.navigation.Navigator

@Composable
fun DialogButton(
    text: String,
    dismissDialogOnClick: Boolean = true,
    style: ButtonStyle = TextButtonStyle(),
    onClick: (() -> Unit)? = null
) = composable {
    val navigator = inject<Navigator>()
    Opacity(opacity = if (onClick != null) 1f else 0.5f) {
        Button(
            text = text.toUpperCase(), // todo find a better way for uppercase
            onClick = onClick?.let {
                {
                    onClick()
                    if (dismissDialogOnClick) navigator.pop()
                }
            },
            style = style
        )
    }
}

@Composable
fun DialogCloseButton(
    text: String,
    style: ButtonStyle = TextButtonStyle()
) = composable {
    DialogButton(
        text = text,
        dismissDialogOnClick = true,
        style = style,
        onClick = {}
    )
}
