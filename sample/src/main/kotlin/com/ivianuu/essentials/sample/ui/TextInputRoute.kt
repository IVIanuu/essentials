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

package com.ivianuu.essentials.sample.ui

import androidx.compose.Observe
import androidx.compose.ambient
import androidx.compose.onActive
import androidx.compose.onDispose
import androidx.compose.remember
import androidx.ui.core.Alignment
import androidx.ui.core.Opacity
import androidx.ui.foundation.Clickable
import androidx.ui.layout.Center
import androidx.ui.layout.Container
import androidx.ui.material.MaterialTheme
import com.github.ajalt.timberkt.d
import com.ivianuu.essentials.ui.common.ScrollPosition
import com.ivianuu.essentials.ui.common.framed
import com.ivianuu.essentials.ui.common.holder
import com.ivianuu.essentials.ui.common.holderFor
import com.ivianuu.essentials.ui.core.KeyboardManagerAmbient
import com.ivianuu.essentials.ui.core.Text
import com.ivianuu.essentials.ui.core.TextField
import com.ivianuu.essentials.ui.core.call
import com.ivianuu.essentials.ui.core.retain
import com.ivianuu.essentials.ui.layout.ScrollableList
import com.ivianuu.essentials.ui.material.FloatingActionButton
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.Route

val TextInputRoute = Route {
    val state = remember { TextInputState() }

    if (!state.searchVisible) {
        state.inputValue = ""
    }

    val items = AllItems.filter {
        state.inputValue.isEmpty() || state.inputValue in it.toLowerCase().trim()
    }

    val keyboardManager = ambient(KeyboardManagerAmbient)
    onDispose { keyboardManager.hideKeyboard() }

    Scaffold(
        topAppBar = {
            TopAppBar(
                title = {
                    if (state.searchVisible) {
                        Container(
                            expanded = true,
                            alignment = Alignment.CenterLeft
                        ) {
                            Clickable(onClick = { keyboardManager.showKeyboard("id") }) {
                                if (state.inputValue.isEmpty()) {
                                    Opacity(0.5f) {
                                        Text(
                                            text = "Search..",
                                            style = MaterialTheme.typography().subtitle1
                                        )
                                    }
                                }
                                TextField(
                                    value = state.inputValue,
                                    onValueChange = { state.inputValue = it },
                                    focusIdentifier = "id",
                                    textStyle = MaterialTheme.typography().subtitle1
                                )
                            }
                        }

                        onActive { keyboardManager.showKeyboard("id") }
                    } else {
                        Text("Text input")
                    }
                }
            )
        },
        body = {
            if (items.isNotEmpty()) {
                val scrollPosition = retain(items) { ScrollPosition() }

                Observe {
                    val lastScrollPosition = holderFor(scrollPosition) { scrollPosition.value }

                    if (lastScrollPosition.value < scrollPosition.value) {
                        keyboardManager.hideKeyboard()
                        if (state.searchVisible && state.inputValue.isEmpty()) {
                            state.searchVisible = false
                        }
                    }
                }

                ScrollableList(
                    position = scrollPosition,
                    items = items
                ) { _, item ->
                    ListItem(
                        title = { Text(item) },
                        onClick = {
                            d { "clicked $item" }
                        }
                    )
                }
            } else {
                Center {
                    Text("No results")
                }
            }
        },
        fab = {
            if (!state.searchVisible) {
                FloatingActionButton(
                    text = "Search",
                    onClick = { state.searchVisible = true }
                )
            }
        }
    )
}

private class TextInputState {
    var searchVisible by framed(false)
    var inputValue by framed("")
}

private val AllItems = (0..100).map { "Item: $it" }