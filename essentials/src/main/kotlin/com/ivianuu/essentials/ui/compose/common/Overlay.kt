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

package com.ivianuu.essentials.ui.compose.common

import androidx.compose.Ambient
import androidx.compose.Composable
import androidx.compose.frames.ModelList
import androidx.ui.core.IntPx
import androidx.ui.core.Layout
import com.ivianuu.essentials.ui.compose.core.composable

@Composable
fun Overlay(
    initialEntries: List<OverlayEntry>
) = composable("Overlay") {

}

class Overlay internal constructor(
    private val _entries: ModelList<OverlayEntry>
) {

    val entries: List<OverlayEntry>
        get() = _entries

    fun add(entry: OverlayEntry) {
        _entries.add(entry)
    }

    fun add(index: Int, entry: OverlayEntry) {
        _entries.add(index, entry)
    }

    fun remove(entry: OverlayEntry) {
        _entries.remove(entry)
    }

}

data class OverlayEntry(
    val isFloating: Boolean,
    val keepState: Boolean,
    val compose: @Composable() () -> Unit
)

val OverlayAmbient = Ambient.of<Overlay>()

@Composable
private fun NavigatorLayout(
    children: @Composable() () -> Unit
) = composable("NavigatorLayout") {
    Layout(children) { measureables, constraints ->
        // force children to fill the whole space
        val childConstraints = constraints.copy(
            minWidth = constraints.maxWidth,
            minHeight = constraints.maxHeight
        )

        // get only visible routes
        val placeables = measureables
            .filter { (it.parentData as NavigatorParentData).isVisible }
            .map { it.measure(childConstraints) }

        layout(constraints.maxWidth, constraints.maxHeight) {
            placeables.forEach { it.place(IntPx.Zero, IntPx.Zero) }
        }
    }
}

private data class NavigatorParentData(val isVisible: Boolean)