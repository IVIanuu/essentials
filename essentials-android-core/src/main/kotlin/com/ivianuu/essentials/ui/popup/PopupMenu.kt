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

package com.ivianuu.essentials.ui.popup

import androidx.compose.Composable
import androidx.compose.Immutable
import androidx.compose.key
import androidx.ui.core.Modifier
import androidx.ui.foundation.Box
import androidx.ui.foundation.ContentGravity
import androidx.ui.foundation.clickable
import androidx.ui.layout.Column
import androidx.ui.layout.height
import androidx.ui.layout.preferredWidthIn
import androidx.ui.unit.dp
import com.ivianuu.essentials.ui.core.currentOrElse
import com.ivianuu.essentials.ui.navigation.NavigatorAmbient
import com.ivianuu.essentials.ui.navigation.RouteAmbient

object PopupMenu {
    @Immutable
    data class Item(
        val onSelected: () -> Unit,
        val content: @Composable () -> Unit
    )
}

@Composable
fun PopupMenu(
    items: List<PopupMenu.Item>,
    style: PopupStyle = PopupStyleAmbient.currentOrElse { DefaultPopupStyle() }
) {
    Popup(style = style) {
        Column {
            val navigator = NavigatorAmbient.current
            val route = RouteAmbient.current
            items.forEach { item ->
                key(item) {
                    PopupMenuItem(
                        onSelected = {
                            navigator.pop(route = route)
                            item.onSelected()
                        },
                        children = item.content
                    )
                }
            }
        }
    }
}

@Composable
private fun PopupMenuItem(
    onSelected: () -> Unit,
    children: @Composable () -> Unit
) {
    Box(
        modifier = Modifier.preferredWidthIn(minWidth = 200.dp)
            .height(48.dp)
            .clickable(onClick = onSelected),
        gravity = ContentGravity.CenterStart
    ) {
        Box(
            gravity = ContentGravity.CenterStart,
            paddingStart = 16.dp,
            paddingEnd = 16.dp,
            children = children
        )
    }
}
