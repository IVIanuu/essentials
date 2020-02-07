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

package com.ivianuu.essentials.ui.common

import androidx.compose.Composable
import androidx.compose.Immutable
import androidx.compose.Model
import androidx.compose.Providers
import androidx.compose.Stable
import androidx.compose.frames.modelListOf
import androidx.compose.key
import androidx.compose.remember
import androidx.compose.staticAmbientOf
import androidx.ui.core.Layout
import androidx.ui.core.ParentData
import androidx.ui.layout.Stack
import androidx.ui.unit.IntPxPosition
import com.ivianuu.essentials.ui.core.tightMax

@Composable
fun Overlay(state: OverlayState = remember { OverlayState() }) {
    Providers(OverlayAmbient provides state) {
        OverlayLayout {
            val visibleEntries = state.entries.filterVisible()

            state.entries
                .filter { it in visibleEntries || it.keepState }
                .map {
                    OverlayEntryParentData(
                        isVisible = it in visibleEntries,
                        entry = it
                    )
                }
                .forEach { parentData ->
                    key(parentData.entry) {
                        ParentData(data = parentData) {
                            AbsorbPointer(absorb = !parentData.isVisible) {
                                Stack {
                                    parentData.entry.content()
                                }
                            }
                        }
                    }
                }
        }
    }
}

@Stable
class OverlayState(initialEntries: List<OverlayEntry>? = null) {

    private val _entries = modelListOf<OverlayEntry>()
    val entries: List<OverlayEntry>
        get() = _entries

    init {
        if (initialEntries != null && _entries.isEmpty()) {
            _entries += initialEntries
        }
    }

    fun add(entry: OverlayEntry) {
        check(entry !in _entries)
        _entries += entry
    }

    fun add(index: Int, entry: OverlayEntry) {
        check(entry !in _entries)
        _entries.add(index, entry)
    }

    fun remove(entry: OverlayEntry) {
        _entries -= entry
    }

    fun move(from: Int, to: Int) {
        _entries.add(to, _entries.removeAt(from))
    }

    fun replace(entries: List<OverlayEntry>) {
        _entries.clear()
        _entries += entries
    }
}

@Model
class OverlayEntry(
    var opaque: Boolean = false,
    var keepState: Boolean = false,
    val content: @Composable () -> Unit
)

val OverlayAmbient =
    staticAmbientOf<OverlayState> { error("No overlay provided") }

@Composable
private fun OverlayLayout(
    children: @Composable () -> Unit
) {
    Layout(children = children) { measurables, constraints ->
        // force children to fill the whole space
        val childConstraints = constraints.tightMax()

        val placeables = measurables
            .filter { (it.parentData as OverlayEntryParentData).isVisible }
            .map { it.measure(childConstraints) }

        val width = constraints.maxWidth
        val height = constraints.maxHeight

        layout(width, height) {
            placeables.forEach { placeable ->
                placeable.place(IntPxPosition.Origin)
            }
        }
    }
}

@Immutable
private data class OverlayEntryParentData(
    val isVisible: Boolean,
    val entry: OverlayEntry
)

private fun List<OverlayEntry>.filterVisible(): List<OverlayEntry> {
    val visibleEntries = mutableListOf<OverlayEntry>()

    for (entry in reversed()) {
        visibleEntries += entry
        if (!entry.opaque) break
    }

    return visibleEntries
}
