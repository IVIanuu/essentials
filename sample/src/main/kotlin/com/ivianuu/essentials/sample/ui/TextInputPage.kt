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

import androidx.compose.Composable
import androidx.compose.getValue
import androidx.compose.mutableStateOf
import androidx.compose.onActive
import androidx.compose.remember
import androidx.compose.setValue
import androidx.ui.core.Alignment
import androidx.ui.core.Modifier
import androidx.ui.core.drawOpacity
import androidx.ui.core.focus.FocusModifier
import androidx.ui.foundation.Text
import androidx.ui.foundation.TextField
import androidx.ui.foundation.TextFieldValue
import androidx.ui.foundation.clickable
import androidx.ui.layout.Stack
import androidx.ui.layout.fillMaxSize
import androidx.ui.material.ExtendedFloatingActionButton
import androidx.ui.material.MaterialTheme
import com.ivianuu.essentials.ui.common.InsettingLazyColumnItems
import com.ivianuu.essentials.ui.layout.center
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.injekt.Transient

@Transient
class TextInputPage {

    @Composable
    operator fun invoke() {
        val state = remember { TextInputState() }

        if (!state.searchVisible) {
            state.inputValue = TextFieldValue()
        }

        val items = AllItems.filter {
            state.inputValue.text.isEmpty() || state.inputValue.text in it.toLowerCase().trim()
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        if (state.searchVisible) {
                            val focusModifier = FocusModifier()

                            Stack(
                                modifier = Modifier.fillMaxSize()
                                    .clickable { focusModifier.requestFocus() }
                            ) {
                                if (state.inputValue.text.isEmpty()) {
                                    Text(
                                        text = "Search..",
                                        style = MaterialTheme.typography.subtitle1,
                                        modifier = Modifier.drawOpacity(0.5f)
                                            .gravity(Alignment.CenterStart) + focusModifier
                                    )
                                }
                                TextField(
                                    value = state.inputValue,
                                    onValueChange = { state.inputValue = it },
                                    textStyle = MaterialTheme.typography.subtitle1,
                                    modifier = Modifier.gravity(Alignment.CenterStart)
                                )
                            }

                            onActive { focusModifier.requestFocus() }
                        } else {
                            Text("Text input")
                        }
                    }
                )
            },
            fab = {
                if (!state.searchVisible) {
                    ExtendedFloatingActionButton(
                        text = { Text("Search") },
                        onClick = { state.searchVisible = true }
                    )
                }
            }
        ) {
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

                InsettingLazyColumnItems(items = items) { item ->
                    ListItem(
                        title = { Text(item) }
                    )
                }
            } else {
                Text(
                    text = "No results",
                    modifier = Modifier.center()
                )
            }
        }
    }

}

private class TextInputState {
    var searchVisible by mutableStateOf(false)
    var inputValue by mutableStateOf(TextFieldValue())
}

private val AllItems = (0..100).map { "Item: $it" }