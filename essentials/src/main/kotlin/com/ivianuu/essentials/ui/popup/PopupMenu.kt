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
import androidx.compose.ambient
import androidx.compose.key
import androidx.ui.core.Alignment
import androidx.ui.core.dp
import androidx.ui.foundation.Clickable
import androidx.ui.layout.Container
import androidx.ui.layout.LayoutHeight
import androidx.ui.layout.LayoutMinWidth
import androidx.ui.layout.LayoutPadding
import androidx.ui.layout.Wrap
import androidx.ui.material.ripple.Ripple
import com.ivianuu.essentials.ui.core.Text
import com.ivianuu.essentials.ui.layout.Column
import com.ivianuu.essentials.ui.layout.WithModifier
import com.ivianuu.essentials.ui.navigation.navigator

// todo add selectable items

object PopupMenu {
    data class Item(
        val onSelected: (() -> Unit)? = null,
        val content: @Composable() () -> Unit
    ) {
        constructor(
            text: String,
            onSelected: (() -> Unit)? = null
        ) : this(onSelected = onSelected, content = {
            Text(
                text
            )
        })
    }
}

@Composable
fun PopupMenu(
    items: List<PopupMenu.Item>,
    style: PopupStyle = ambient(PopupStyleAmbient)
) {
    Popup(style = style) {
        Column {
            val navigator = navigator
            items.forEach { item ->
                key(item) {
                    PopupMenuItem(
                        content = item.content,
                        onSelected = {
                            navigator.popTop()
                            item.onSelected?.invoke()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun <T> PopupMenu(
    items: List<T>,
    onSelected: (T) -> Unit,
    style: PopupStyle = ambient(PopupStyleAmbient),
    item: @Composable() (T) -> Unit
) {
    PopupMenu(
        style = style,
        items = items.map { value ->
            PopupMenu.Item(
                onSelected = { onSelected(value) },
                content = { item(value) }
            )
        }
    )
}

// todo public
@Composable
private fun PopupMenuItem(
    onSelected: (() -> Unit)? = null,
    content: @Composable() () -> Unit
) {
    Ripple(bounded = true) {
        Clickable(
            onClick = onSelected,
            children = {
                Container(
                    modifier = LayoutMinWidth(200.dp) + LayoutHeight(48.dp),
                    alignment = Alignment.CenterLeft
                ) {
                    Wrap(Alignment.CenterLeft) {
                        WithModifier(
                            modifier = LayoutPadding(left = 16.dp, right = 16.dp),
                            children = content
                        )
                    }
                }
            }
        )
    }
}
