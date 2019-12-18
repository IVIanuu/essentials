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

import androidx.compose.onActive
import androidx.compose.remember
import androidx.ui.core.Alignment
import androidx.ui.core.Opacity
import androidx.ui.core.Text
import androidx.ui.core.TextField
import androidx.ui.foundation.Clickable
import androidx.ui.foundation.ScrollerPosition
import androidx.ui.layout.Center
import androidx.ui.layout.Container
import androidx.ui.material.FloatingActionButton
import androidx.ui.material.MaterialTheme
import com.github.ajalt.timberkt.d
import com.ivianuu.essentials.ui.common.framed
import com.ivianuu.essentials.ui.common.hideKeyboard
import com.ivianuu.essentials.ui.common.ref
import com.ivianuu.essentials.ui.common.showKeyboard
import com.ivianuu.essentials.ui.layout.ScrollableList
import com.ivianuu.essentials.ui.material.EsTopAppBar
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.SimpleListItem
import com.ivianuu.essentials.ui.navigation.Route

val TextInputRoute = Route {
    val state = remember { TextInputState() }

    if (!state.searchVisible) {
        state.inputValue = ""
    }

    val items = AllItems.filter {
        state.inputValue.isEmpty() || state.inputValue in it.toLowerCase().trim()
    }

    val hideKeyboard = hideKeyboard()
    val showKeyboard = showKeyboard("id")

    Scaffold(
        topAppBar = {
            EsTopAppBar(
                title = {
                    if (state.searchVisible) {
                        Container(
                            expanded = true,
                            alignment = Alignment.CenterLeft
                        ) {
                            Clickable(onClick = { showKeyboard() }) {
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

                        onActive { showKeyboard() }
                    } else {
                        Text("Text input")
                    }
                }
            )
        },
        body = {
            if (items.isNotEmpty()) {
                val scrollerPosition = remember(items) { ScrollerPosition() }
                val lastScrollPosition =
                    ref { scrollerPosition.value }

                if (scrollerPosition.value != lastScrollPosition.value) {
                    hideKeyboard()
                    if (state.searchVisible && state.inputValue.isEmpty()) {
                        state.searchVisible = false
                    }
                }

                ScrollableList(
                    // todo scrollerPosition = scrollerPosition,
                    items = items
                ) { _, item ->
                    SimpleListItem(
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
            FloatingActionButton(
                text = "Toggle search",
                onClick = { state.searchVisible = !state.searchVisible }
            )
        }
    )
}

private class TextInputState {
    var searchVisible by framed(false)
    var inputValue by framed("")
}

private val AllItems = (0..100).map { "Item: $it" }