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
import androidx.compose.unaryPlus
import androidx.ui.core.IntPx
import androidx.ui.core.Layout
import androidx.ui.core.LayoutNode
import androidx.ui.core.Px
import androidx.ui.core.Text
import androidx.ui.core.looseMin
import androidx.ui.core.round
import androidx.ui.layout.CrossAxisAlignment
import androidx.ui.layout.MainAxisAlignment
import androidx.ui.layout.Row
import androidx.ui.material.Button
import com.github.ajalt.timberkt.d
import com.ivianuu.essentials.ui.compose.common.scrolling.Scroller
import com.ivianuu.essentials.ui.compose.composeControllerRoute
import com.ivianuu.essentials.ui.compose.core.childManager
import com.ivianuu.essentials.ui.compose.material.SimpleListItem

val homeRoute = composeControllerRoute {
    Scroller {
        val childManager = +childManager()

        val indices = +memo { mutableListOf<Int>() }

        d { "invoke home route" }

        fun addChild() {
            val index = (indices.max() ?: 0) + 1
            indices += index
            childManager.add(index) {
                d { "invoke $index" }
                +onActive {
                    d { "state $index on active" }
                    onDispose { d { " state $index on dispose" } }
                }

                SimpleListItem(
                    title = { Text("Item $index") },
                    onClick = {
                        d { "remove item at $index" }
                        childManager.remove(index)
                        indices -= index
                    }
                )
            }
        }

        fun remove() {
            if (indices.isNotEmpty()) {
                val index = indices.shuffled().first()
                childManager.remove(index)
                indices -= index
            }
        }

        fun clearChildren() {
            childManager.clear()
            indices.clear()
        }

        fun addActions() {
            childManager.add("actions") {
                d { "invoke actions" }
                +onActive {
                    d { "state actions on active" }
                    onDispose { d { "state actions on dispose" } }
                }
                Row(
                    mainAxisAlignment = MainAxisAlignment.Center,
                    crossAxisAlignment = CrossAxisAlignment.Center
                ) {
                    Button(
                        text = "Add",
                        onClick = { addChild() }
                    )
                    Button(
                        text = "Add ten",
                        onClick = { repeat(10) { addChild() } }
                    )
                    Button(
                        text = "Remove",
                        onClick = { remove() }
                    )
                    Button(
                        text = "Clear",
                        onClick = { clearChildren() }
                    )
                }
            }
        }

        Layout(children = {}) { measurables, constraints ->
            val layoutNode = (this as LayoutNode.InnerMeasureScope).layoutNode
            childManager.setLayoutNode(layoutNode)

            d { "invoke measure" }

            if (layoutNode.layoutChildren.isEmpty()) {
                addActions()
            }

            val layoutOffsets = mutableListOf<Px>()
            var layoutOffset = Px.Zero
            layoutNode.layoutChildren.forEach { child ->
                child.measure(constraints.looseMin())
                layoutOffsets += layoutOffset
                layoutOffset += child.height
            }

            layout(constraints.maxWidth, constraints.maxHeight) {
                d { "invoke layout" }

                layoutNode.layoutChildren.forEachIndexed { index, placeable ->
                    placeable.place(IntPx.Zero, layoutOffsets[index].round())
                }
            }
        }
    }
}