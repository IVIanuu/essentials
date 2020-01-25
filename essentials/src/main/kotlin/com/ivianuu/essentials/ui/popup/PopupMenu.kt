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
import androidx.ui.core.Alignment
import androidx.ui.layout.Container
import androidx.ui.layout.LayoutHeight
import androidx.ui.layout.LayoutPadding
import androidx.ui.layout.LayoutWidth
import androidx.ui.layout.Wrap
import androidx.ui.material.ripple.Ripple
import androidx.ui.unit.dp
import com.ivianuu.essentials.ui.core.Clickable
import com.ivianuu.essentials.ui.core.Text
import com.ivianuu.essentials.ui.layout.Column
import com.ivianuu.essentials.ui.navigation.NavigatorAmbient

// todo add selectable items

object PopupMenu {
    @Immutable
    data class Item(
        val onSelected: (() -> Unit)? = null,
        val content: @Composable () -> Unit
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
    style: PopupStyle = PopupStyleAmbient.current
) {
    Popup(style = style) {
        Column {
            val navigator = NavigatorAmbient.current
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
    style: PopupStyle = PopupStyleAmbient.current,
    item: @Composable (T) -> Unit
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
    content: @Composable () -> Unit
) {
    Ripple(bounded = true) {
        Clickable(
            onClick = onSelected,
            children = {
                Container(
                    modifier = LayoutWidth.Min(200.dp) + LayoutHeight(48.dp),
                    alignment = Alignment.CenterLeft
                ) {
                    Wrap(Alignment.CenterLeft) {
                        Container(
                            modifier = LayoutPadding(left = 16.dp, right = 16.dp),
                            children = content
                        )
                    }
                }
            }
        )
    }
}
