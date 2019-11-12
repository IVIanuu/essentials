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

import androidx.compose.memo
import androidx.compose.onActive
import androidx.compose.state
import androidx.compose.unaryPlus
import androidx.ui.core.Alignment
import androidx.ui.core.Opacity
import androidx.ui.core.Text
import androidx.ui.core.TextField
import androidx.ui.foundation.Clickable
import androidx.ui.layout.Center
import androidx.ui.layout.Column
import androidx.ui.layout.Container
import androidx.ui.material.FloatingActionButton
import androidx.ui.material.themeTextStyle
import com.github.ajalt.timberkt.d
import com.ivianuu.essentials.ui.compose.common.hideKeyboard
import com.ivianuu.essentials.ui.compose.common.scrolling.ScrollPosition
import com.ivianuu.essentials.ui.compose.common.scrolling.Scroller
import com.ivianuu.essentials.ui.compose.common.showKeyboard
import com.ivianuu.essentials.ui.compose.composeControllerRoute
import com.ivianuu.essentials.ui.compose.core.composable
import com.ivianuu.essentials.ui.compose.core.ref
import com.ivianuu.essentials.ui.compose.material.EsTopAppBar
import com.ivianuu.essentials.ui.compose.material.Scaffold
import com.ivianuu.essentials.ui.compose.material.SimpleListItem
import com.ivianuu.essentials.ui.navigation.director.controllerRouteOptions
import com.ivianuu.essentials.ui.navigation.director.vertical

val textInputRoute = composeControllerRoute(
    options = controllerRouteOptions().vertical()
) {
    val (searchVisible, setSearchVisible) = +state { false }
    val (inputValue, setInputValue) = +state { "" }

    if (!searchVisible) {
        setInputValue("")
    }

    val items = AllItems.filter {
        inputValue.isEmpty() || inputValue in it.toLowerCase().trim()
    }

    val hideKeyboard = +hideKeyboard()
    val showKeyboard = +showKeyboard("id")

    Scaffold(
        topAppBar = {
            EsTopAppBar(
                title = {
                    if (searchVisible) {
                        Container(
                            expanded = true,
                            alignment = Alignment.CenterLeft
                        ) {
                            Clickable(onClick = { showKeyboard() }) {
                                if (inputValue.isEmpty()) {
                                    Opacity(0.5f) {
                                        Text(
                                            text = "Search..",
                                            style = +themeTextStyle { subtitle1 })
                                    }
                                }
                                TextField(
                                    value = inputValue,
                                    onValueChange = setInputValue,
                                    focusIdentifier = "id",
                                    textStyle = +themeTextStyle { subtitle1 }
                                )
                            }
                        }

                        +onActive { showKeyboard() }
                    } else {
                        Text("Text input")
                    }
                }
            )
        },
        body = {
            if (items.isNotEmpty()) {
                composable("results") {
                    val scrollPosition = +memo(items) { ScrollPosition() }
                    val lastScrollPosition = +ref { scrollPosition.value }

                    if (scrollPosition.value != lastScrollPosition.value) {
                        hideKeyboard()
                        if (searchVisible && inputValue.isEmpty()) {
                            setSearchVisible(false)
                        }
                    }

                    Scroller(position = scrollPosition) {
                        Column {
                            items.forEach {
                                SimpleListItem(
                                    title = { Text(it) },
                                    onClick = {
                                        d { "clicked $it" }
                                    }
                                )
                            }
                        }
                    }
                }
            } else {
                composable("empty") {
                    Center {
                        Text("No results")
                    }
                }
            }
        },
        fab = {
            FloatingActionButton(
                text = "Toggle search",
                onClick = { setSearchVisible(!searchVisible) }
            )
        }
    )
}

private val AllItems = (0..100).map { "Item: $it" }