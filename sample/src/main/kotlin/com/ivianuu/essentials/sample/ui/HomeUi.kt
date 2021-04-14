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

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ivianuu.essentials.ui.core.localVerticalInsetsPadding
import com.ivianuu.essentials.ui.dialog.ColorPickerPalette
import com.ivianuu.essentials.ui.material.HorizontalDivider
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.KeyUi
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.ui.navigation.RootKey
import com.ivianuu.essentials.ui.popup.PopupMenu
import com.ivianuu.essentials.ui.popup.PopupMenuButton
import com.ivianuu.essentials.util.Toaster
import com.ivianuu.injekt.Given

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
        title = { Text(item.title) },
        leading = {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(color, CircleShape)
            )
        },
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

data class HomeItem(val title: String, val keyFactory: (Color) -> Key<Nothing>)
