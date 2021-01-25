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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.runtime.savedinstancestate.rememberSavedInstanceState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ivianuu.essentials.store.DispatchAction
import com.ivianuu.essentials.ui.Remembered
import com.ivianuu.essentials.ui.animatedstack.animation.SharedElement
import com.ivianuu.essentials.ui.common.InsettingLazyColumnFor
import com.ivianuu.essentials.ui.dialog.ColorPickerPalette
import com.ivianuu.essentials.ui.material.HorizontalDivider
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.HomeKeyBinding
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.KeyUiBinding
import com.ivianuu.essentials.ui.navigation.NavigationAction
import com.ivianuu.essentials.ui.popup.PopupMenu
import com.ivianuu.essentials.ui.popup.PopupMenuButton
import com.ivianuu.essentials.util.showToast
import com.ivianuu.injekt.*

@HomeKeyBinding @Given class HomeKey

@KeyUiBinding<HomeKey>
@GivenFun
@Composable
fun HomeScreen(
    @Given dispatchNavigationAction: DispatchAction<NavigationAction>,
    @Given items: @Remembered Set<HomeItem>,
    @Given showToast: showToast,
) {
    val finalItems = remember(items) { items.sortedBy { it.title } }
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
                            PopupMenu.Item(onSelected = { showToast("Selected $title") }) {
                                Text(title)
                            }
                        }
                    )
                }
            )
        }
    ) {
        InsettingLazyColumnFor(items = finalItems) { item ->
            val color = key(item) {
                rememberSavedInstanceState(item) {
                    ColorPickerPalette.values()
                        .filter { it != ColorPickerPalette.Black && it != ColorPickerPalette.White }
                        .shuffled()
                        .first()
                        .front
                }
            }

            HomeItem(
                item = item,
                color = color,
                onClick = {
                    dispatchNavigationAction(NavigationAction.Push(item.keyFactory(color)))
                }
            )

            if (finalItems.indexOf(item) != finalItems.lastIndex) {
                HorizontalDivider(modifier = Modifier.padding(start = 72.dp))
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
            SharedElement(item.title) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(color, CircleShape)
                )
            }
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

data class HomeItem(val title: String, val keyFactory: (Color) -> Key)

@Qualifier annotation class HomeItemBinding

@Macro
@GivenSetElement
fun <T : @HomeItemBinding HomeItem> homeItemBindingImpl(@Given instance: T): HomeItem =
    instance
