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
import androidx.ui.foundation.Clickable
import androidx.ui.foundation.ContentGravity
import androidx.ui.layout.padding
import androidx.ui.layout.preferredHeight
import androidx.ui.layout.preferredWidthIn
import androidx.ui.unit.dp
import com.ivianuu.essentials.ui.core.Text
import com.ivianuu.essentials.ui.core.currentOrElse
import com.ivianuu.essentials.ui.layout.Column
import com.ivianuu.essentials.ui.material.ripple
import com.ivianuu.essentials.ui.navigation.NavigatorAmbient

object PopupMenu {
    @Immutable
    data class Item(
        val onSelected: () -> Unit,
        val content: @Composable () -> Unit
    ) {
        constructor(
            onSelected: () -> Unit,
            title: String
        ) : this(onSelected = onSelected, content = {
            Text(title)
        })
    }
}

@Composable
fun PopupMenu(
    items: List<PopupMenu.Item>,
    style: PopupStyle = PopupStyleAmbient.currentOrElse { DefaultPopupStyle() }
) {
    Popup(style = style) {
        Column {
            val navigator = NavigatorAmbient.current
            items.forEach { item ->
                key(item) {
                    PopupMenuItem(
                        onSelected = {
                            navigator.popTop()
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
    Clickable(onClick = onSelected, modifier = Modifier.ripple()) {
        Box(
            modifier = Modifier.preferredWidthIn(minWidth = 200.dp)
                .preferredHeight(48.dp)
                .padding(start = 16.dp, end = 16.dp),
            gravity = ContentGravity.CenterStart,
            children = children
        )
    }
}
