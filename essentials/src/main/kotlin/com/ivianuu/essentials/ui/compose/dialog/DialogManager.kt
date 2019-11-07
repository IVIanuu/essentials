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

package com.ivianuu.essentials.ui.compose.dialog

import androidx.compose.Ambient
import androidx.compose.Composable
import androidx.compose.ambient
import androidx.compose.memo
import androidx.compose.unaryPlus
import androidx.ui.core.Alignment
import androidx.ui.core.dp
import androidx.ui.core.gesture.PressGestureDetector
import androidx.ui.foundation.ColoredRect
import androidx.ui.foundation.shape.corner.RoundedCornerShape
import androidx.ui.graphics.Color
import androidx.ui.layout.Padding
import androidx.ui.layout.Wrap
import androidx.ui.material.surface.Card
import com.ivianuu.essentials.ui.compose.core.composable
import com.ivianuu.essentials.ui.compose.material.Scaffold
import com.ivianuu.essentials.ui.compose.material.ScaffoldAmbient

fun DialogManager(children: @Composable() () -> Unit) {
    val scaffold = +ambient(ScaffoldAmbient)
    val dialogManager = +memo { DialogManager(scaffold) }
    DialogManagerAmbient.Provider(value = dialogManager, children = children)
}

class DialogManager internal constructor(private val scaffold: Scaffold) {

    private var visibleDialogs = 0
    private var showingScrim = false
    private var removeScrim: (() -> Unit)? = null

    fun showDialog(
        content: @Composable() (() -> Unit) -> Unit
    ) {
        ++visibleDialogs
        updateScrimState()

        scaffold.addOverlay { removeOverlay ->
            val dismissDialog = {
                removeOverlay()
                --visibleDialogs
                updateScrimState()
            }

            PressGestureDetector(
                onPress = { dismissDialog() }
            ) {
                Wrap(alignment = Alignment.Center) {
                    Padding(padding = DialogPadding) {
                        PressGestureDetector {
                            Card(shape = RoundedCornerShape(size = DialogCornerRadius)) {
                                Padding(padding = DialogContentPadding) {
                                    content(dismissDialog)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun updateScrimState() {
        if (visibleDialogs > 0 && !showingScrim) {
            showingScrim = true
            scaffold.addOverlay { removeOverlay ->
                removeScrim = removeOverlay
                DialogScrim()
            }
        } else if (visibleDialogs == 0 && showingScrim) {
            removeScrim?.invoke()
            showingScrim = false
        }
    }
}

val DialogManagerAmbient = Ambient.of<DialogManager>()

@Composable
private fun DialogScrim() = composable("DialogScrim") {
    ColoredRect(Color.Black.copy(alpha = 0.7f))
}

private val DialogCornerRadius = 8.dp
private val DialogPadding = 72.dp
private val DialogContentPadding = 16.dp