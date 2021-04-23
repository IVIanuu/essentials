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

package com.ivianuu.essentials.sample.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.unit.*
import com.ivianuu.essentials.colorpicker.*
import com.ivianuu.essentials.ui.animation.*
import com.ivianuu.essentials.ui.animation.transition.*
import com.ivianuu.essentials.ui.core.*
import com.ivianuu.essentials.ui.dialog.*
import com.ivianuu.essentials.ui.material.*
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.essentials.ui.popup.*
import com.ivianuu.essentials.util.*
import com.ivianuu.injekt.*

@Given
class HomeKey : RootKey

@Given
fun homeUi(
    @Given navigator: Navigator,
    @Given itemsFactory: () -> Set<HomeItem>,
    @Given toaster: Toaster,
): KeyUi<HomeKey> = {
    val finalItems = remember { itemsFactory().sortedBy { it.title } }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Home") },
                actions = {
                    PopupMenuButton(
                        items = listOf(
                            "Option 1",
                            "Option 2",
                            "Option 3"
                        ).map { title ->
                            PopupMenu.Item(onSelected = { toaster("Selected $title") }) {
                                Text(title)
                            }
                        }
                    )
                }
            )
        }
    ) {
        LazyColumn(contentPadding = localVerticalInsetsPadding()) {
            items(finalItems) { item ->
                val color = rememberSaveable(item) {
                    ColorPickerPalette.values()
                        .filter { it != ColorPickerPalette.BLACK && it != ColorPickerPalette.WHITE }
                        .shuffled()
                        .first()
                        .front
                }

                HomeItem(
                    item = item,
                    color = color,
                    onClick = { navigator.push(item.keyFactory(color)) }
                )

                if (finalItems.indexOf(item) != finalItems.lastIndex) {
                    HorizontalDivider(modifier = Modifier.padding(start = 72.dp))
                }
            }
        }
    }
}

@Composable
private fun HomeItem(
    color: Color,
    onClick: () -> Unit,
    item: HomeItem,
) {
    ListItem(
        title = {
            SharedElement(key = "title ${item.title}", isStart = true) {
                Text(
                    item.title,
                    style = MaterialTheme.typography.subtitle1
                )
            }
        },
        leading = { SharedCircleBadge("color ${item.title}", color, 40.dp, true) },
        trailing = {
            PopupMenuButton(
                items = listOf(1, 2, 3)
                    .map { index ->
                        PopupMenu.Item(onSelected = {}) {
                            Text(index.toString())
                        }
                    }
            )
        },
        onClick = onClick
    )
}

@Composable
fun SharedCircleBadge(
    key: Any,
    color: Color,
    size: Dp,
    isStart: Boolean
) {
    SharedElement(key = key, isStart = isStart) {
        val fraction = LocalSharedElementTransitionFraction.current
        Box(
            modifier = Modifier
                .size(size)
                .background(color, RoundedCornerShape((size / 2) * (1f - fraction)))
        )
    }
}

data class HomeItem(val title: String, val keyFactory: (Color) -> Key<Nothing>)
