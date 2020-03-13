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

package com.ivianuu.essentials.android.ui.dialog

import androidx.compose.Composable
import androidx.ui.core.drawOpacity
import com.ivianuu.essentials.android.ui.core.currentOrNull
import com.ivianuu.essentials.android.ui.material.Button
import com.ivianuu.essentials.android.ui.material.ButtonStyle
import com.ivianuu.essentials.android.ui.material.ButtonStyleAmbient
import com.ivianuu.essentials.android.ui.material.TextButtonStyle
import com.ivianuu.essentials.android.ui.navigation.NavigatorAmbient

@Composable
fun DialogButton(
    onClick: () -> Unit,
    enabled: Boolean = true,
    dismissDialogOnClick: Boolean = true,
    style: ButtonStyle = ButtonStyleAmbient.currentOrNull ?: TextButtonStyle(),
    children: @Composable () -> Unit
) {
    val navigator = NavigatorAmbient.current
    Button(
        onClick = {
            onClick()
            if (dismissDialogOnClick) navigator.popTop()
        },
        enabled = enabled,
        style = style,
        modifier = drawOpacity(opacity = if (enabled) 1f else 0.5f),
        children = children
    )
}

@Composable
fun DialogCloseButton(
    style: ButtonStyle = TextButtonStyle(),
    children: @Composable() () -> Unit
) {
    DialogButton(
        onClick = {},
        dismissDialogOnClick = true,
        style = style,
        children = children
    )
}