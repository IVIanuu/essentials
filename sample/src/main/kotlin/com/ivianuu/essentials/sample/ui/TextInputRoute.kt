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

import androidx.compose.Model
import androidx.compose.Observe
import androidx.compose.onActive
import androidx.compose.onDispose
import androidx.compose.remember
import androidx.ui.core.Alignment
import androidx.ui.core.AnimationClockAmbient
import androidx.ui.core.drawOpacity
import androidx.ui.foundation.Clickable
import androidx.ui.foundation.animation.FlingConfig
import androidx.ui.layout.Center
import androidx.ui.layout.Container
import androidx.ui.material.MaterialTheme
import com.github.ajalt.timberkt.d
import com.ivianuu.essentials.android.ui.common.ScrollableList
import com.ivianuu.essentials.android.ui.common.ScrollableState
import com.ivianuu.essentials.android.ui.common.holderFor
import com.ivianuu.essentials.android.ui.core.KeyboardManagerAmbient
import com.ivianuu.essentials.android.ui.core.Text
import com.ivianuu.essentials.android.ui.core.TextField
import com.ivianuu.essentials.android.ui.core.retain
import com.ivianuu.essentials.android.ui.material.FloatingActionButton
import com.ivianuu.essentials.android.ui.material.ListItem
import com.ivianuu.essentials.android.ui.material.Scaffold
import com.ivianuu.essentials.android.ui.material.TopAppBar
import com.ivianuu.essentials.android.ui.navigation.Route

val TextInputRoute = Route {
    val state = remember { TextInputState() }

    if (!state.searchVisible) {
        state.inputValue = ""
    }

    val items = AllItems.filter {
        state.inputValue.isEmpty() || state.inputValue in it.toLowerCase().trim()
    }

    val keyboardManager = KeyboardManagerAmbient.current
    onDispose { keyboardManager.hideKeyboard() }

    Scaffold(
        topAppBar = {
            TopAppBar(
                title = {
                    if (state.searchVisible) {
                        Container(
                            expanded = true,
                            alignment = Alignment.CenterStart
                        ) {
                            Clickable(onClick = { keyboardManager.showKeyboard("id") }) {
                                if (state.inputValue.isEmpty()) {
                                    Text(
                                        text = "Search..",
                                        style = MaterialTheme.typography().subtitle1,
                                        modifier = drawOpacity(0.5f)
                                    )
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
                val animationClock = AnimationClockAmbient.current
                val flingConfig = FlingConfig()
                val scrollPosition = retain(items) {
                    ScrollableState(animationClock, flingConfig = flingConfig)
                }

                Observe {
                    val lastScrollPosition = holderFor(scrollPosition) { scrollPosition.value }

                    if (lastScrollPosition.value < scrollPosition.value) {
                        keyboardManager.hideKeyboard()
                        if (state.searchVisible && state.inputValue.isEmpty()) {
                            state.searchVisible = false
                        }
                    }
                }

                ScrollableList(items = items) { _, item ->
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
                    text = { Text("Search") },
                    onClick = { state.searchVisible = true }
                )
            }
        }
    )
}

@Model
private class TextInputState(
    var searchVisible: Boolean = false,
    var inputValue: String = ""
)

private val AllItems = (0..100).map { "Item: $it" }