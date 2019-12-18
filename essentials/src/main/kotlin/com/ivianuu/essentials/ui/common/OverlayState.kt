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

import androidx.compose.Ambient
import androidx.compose.Composable
import androidx.compose.Observe
import androidx.compose.frames.modelListOf
import androidx.compose.key
import androidx.compose.remember
import androidx.ui.core.IntPx
import androidx.ui.core.Layout
import androidx.ui.core.ParentData
import androidx.ui.core.tightMax
import com.ivianuu.essentials.ui.core.Stable

@Composable
fun Overlay(state: OverlayState = remember { OverlayState() }) {
    OverlayAmbient.Provider(value = state) {
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
                        Observe {
                            ParentData(data = parentData) {
                                AbsorbPointer(absorb = !parentData.isVisible) {
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
class OverlayState(initialEntries: List<OverlayEntry> = emptyList()) {

    private val _entries = modelListOf<OverlayEntry>()
    val entries: List<OverlayEntry>
        get() = _entries

    init {
        if (_entries.isEmpty()) {
            _entries += initialEntries
        }
    }

    fun add(entry: OverlayEntry) {
        check(entry !in _entries)
        _entries.add(entry)
    }

    fun add(index: Int, entry: OverlayEntry) {
        check(entry !in _entries)
        _entries.add(index, entry)
    }

    fun remove(entry: OverlayEntry) {
        _entries.remove(entry)
    }

    fun move(from: Int, to: Int) {
        _entries.add(to, _entries.removeAt(from))
    }

    fun replace(entries: List<OverlayEntry>) {
        _entries.clear()
        _entries += entries
    }

}

@Stable
class OverlayEntry(
    opaque: Boolean = false,
    keepState: Boolean = false,
    val content: @Composable() () -> Unit
) {
    var opaque by framed(opaque)
    var keepState by framed(keepState)
}

val OverlayAmbient = Ambient.of<OverlayState>()

@Composable
private fun OverlayLayout(
    children: @Composable() () -> Unit
) {
    Layout(children = children) { measureables, constraints ->
        // force children to fill the whole space
        val childConstraints = constraints.tightMax()

        // get only visible routes
        val placeables = measureables
            .map { it.measure(childConstraints) to it.parentData as OverlayEntryParentData }

        val width = constraints.maxWidth
        val height = constraints.maxHeight

        layout(width, height) {
            placeables.forEach { (placeable, parentData) ->
                placeable.place(
                    x = if (parentData.isVisible) IntPx.Zero else width,
                    y = if (parentData.isVisible) IntPx.Zero else height
                )
            }
        }
    }
}

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
