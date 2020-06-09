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

package com.ivianuu.essentials.ui.animatedstack

import androidx.compose.Composable
import androidx.compose.Immutable
import androidx.compose.Stable
import androidx.compose.getValue
import androidx.compose.key
import androidx.compose.mutableStateOf
import androidx.compose.setValue
import androidx.ui.core.Layout
import androidx.ui.core.Modifier
import androidx.ui.core.hasBoundedHeight
import androidx.ui.core.hasBoundedWidth
import androidx.ui.core.tag
import androidx.ui.layout.Stack
import androidx.ui.unit.IntPxPosition
import com.ivianuu.essentials.ui.common.absorbPointer

@Composable
fun StatefulStack(
    modifier: Modifier = Modifier,
    entries: List<StatefulStackEntry>
) {
    println("stateful stack with ${entries.toList()}")
    Layout(children = {
        val visibleEntries = entries.filterVisible()
        entries
            .filter { it in visibleEntries || it.keepState }
            .map {
                StackfulStackEntryTag(
                    isVisible = it in visibleEntries,
                    entry = it
                )
            }
            .forEach { tag ->
                key(tag.entry) {
                    Stack(
                        modifier = Modifier
                            .tag(tag)
                            .absorbPointer(absorb = !tag.isVisible)
                    ) {
                        tag.entry.content()
                    }
                }
            }
    }, modifier = modifier) { measurables, constraints, _ ->
        // force children to fill the whole space
        val childConstraints = constraints.copy(
            minWidth = if (constraints.hasBoundedWidth) constraints.maxWidth else constraints.minWidth,
            minHeight = if (constraints.hasBoundedHeight) constraints.maxHeight else constraints.minHeight
        )

        val placeables = measurables
            .filter { (it.tag as StackfulStackEntryTag).isVisible }
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

@Stable
class StatefulStackEntry(
    opaque: Boolean = false,
    keepState: Boolean = false,
    content: @Composable () -> Unit
) {
    var opaque by mutableStateOf(opaque)
    var keepState by mutableStateOf(keepState)
    var content by mutableStateOf(content)
}

@Immutable
private data class StackfulStackEntryTag(
    val isVisible: Boolean,
    val entry: StatefulStackEntry
)

private fun List<StatefulStackEntry>.filterVisible(): List<StatefulStackEntry> {
    val visibleEntries = mutableListOf<StatefulStackEntry>()

    for (entry in reversed()) {
        visibleEntries += entry
        if (!entry.opaque) break
    }

    return visibleEntries
}
