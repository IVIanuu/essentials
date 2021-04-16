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

package com.ivianuu.essentials.ui.popup

import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material.ripple.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.layout.*
import androidx.compose.ui.unit.*
import com.ivianuu.essentials.ui.*
import com.ivianuu.injekt.scope.*

@Composable
fun PopupMenuButton(
    items: List<PopupMenu.Item>,
    onCancel: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = Modifier
            .size(size = 40.dp)
            .popupClickable(
                items = items,
                onCancel = onCancel,
                indication = rememberRipple(bounded = false)
            )
            .then(modifier),
        contentAlignment = Alignment.Center
    ) {
        Icon(Icons.Default.MoreVert, null)
    }
}

@Composable
fun Modifier.popupClickable(
    items: List<PopupMenu.Item>,
    onCancel: (() -> Unit)? = null,
    indication: Indication = LocalIndication.current,
) = composed {
    val dependencies = LocalUiGivenScope.current.element<PopupMenuComponent>()

    var coordinates by remember { mutableStateOf<LayoutCoordinates?>(null) }

    onGloballyPositioned { coordinates = it }
        .clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = indication
        ) {
            dependencies.navigator.push(
                PopupKey(
                    position = coordinates!!.boundsInRoot(),
                    onCancel = onCancel
                ) {
                    PopupMenu(items = items)
                }
            )
        }
}
