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

import androidx.compose.getValue
import androidx.compose.mutableStateOf
import androidx.compose.remember
import androidx.compose.setValue
import androidx.ui.core.Alignment
import androidx.ui.core.Modifier
import androidx.ui.core.drawOpacity
import androidx.ui.foundation.Box
import androidx.ui.foundation.ContentGravity
import androidx.ui.foundation.TextField
import androidx.ui.foundation.TextFieldValue
import androidx.ui.foundation.clickable
import androidx.ui.layout.Stack
import androidx.ui.layout.fillMaxSize
import androidx.ui.material.MaterialTheme
import com.ivianuu.essentials.ui.common.AdapterList
import com.ivianuu.essentials.ui.core.Text
import com.ivianuu.essentials.ui.material.FloatingActionButton
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.Route

val TextInputRoute = Route {
    val state = remember { TextInputState() }

    if (!state.searchVisible) {
        state.inputValue = TextFieldValue()
    }

    val items = AllItems.filter {
        state.inputValue.text.isEmpty() || state.inputValue.text in it.toLowerCase().trim()
    }

    Scaffold(
        topAppBar = {
            TopAppBar(
                title = {
                    if (state.searchVisible) {
                        Stack(
                            modifier = Modifier.fillMaxSize()
                                .clickable { /*keyboardManager.showKeyboard("id")*/ }
                        ) {
                            if (state.inputValue.text.isEmpty()) {
                                Text(
                                    text = "Search..",
                                    textStyle = MaterialTheme.typography.subtitle1,
                                    modifier = Modifier.drawOpacity(0.5f)
                                        .gravity(Alignment.CenterStart)
                                )
                            }
                            TextField(
                                value = state.inputValue,
                                onValueChange = { state.inputValue = it },
                                textStyle = MaterialTheme.typography.subtitle1,
                                modifier = Modifier.gravity(Alignment.CenterStart)
                            )
                        }

                        //onActive { keyboardManager.showKeyboard("id") }
                    } else {
                        Text("Text input")
                    }
                }
            )
        },
        body = {
            if (items.isNotEmpty()) {
                /*val animationClock = AnimationClockAmbient.current
                val flingConfig = FlingConfig()
                val scrollerPosition = retain(items) { ScrollerPosition() }

                val lastScrollPosition = holderFor(scrollerPosition) { scrollerPosition.value }

                if (lastScrollPosition.value < scrollerPosition.value) {
                    //keyboardManager.hideKeyboard()
                    if (state.searchVisible && state.inputValue.text.isEmpty()) {
                        state.searchVisible = false
                    }
                }*/

                AdapterList(data = items) { item ->
                    ListItem(
                        title = { Text(item) }
                    )
                }
            } else {
                Box(modifier = Modifier.fillMaxSize(), gravity = ContentGravity.Center) {
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

private class TextInputState {
    var searchVisible by mutableStateOf(false)
    var inputValue by mutableStateOf(TextFieldValue())
}

private val AllItems = (0..100).map { "Item: $it" }