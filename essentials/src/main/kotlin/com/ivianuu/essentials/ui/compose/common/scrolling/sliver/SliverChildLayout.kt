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

package com.ivianuu.essentials.ui.compose.common.scrolling.sliver

import androidx.ui.core.LayoutNode
import androidx.ui.core.Px

/*
@Composable
fun SliverChildLayout(
    constraints: SliverConstraints,
    geometry: SliverGeometry,
    children: @Composable() () -> Unit
) = composable("SliverChildLayout") {
    Layout(children = children) { measureables, incomingConstraints ->
        val placeables = mutableListOf<Placeable>()
        val parentDatas = mutableListOf<SliverChildParentData>()

        d { "sliver child incoming constraints $incomingConstraints" }

        for (index in measureables.indices) {
            val measureable = measureables[index]
            val parentData = measureable.parentData as SliverChildParentData
            val childConstraints = when (constraints.mainAxisDirection) {
                Direction.LEFT, Direction.RIGHT -> Constraints(
                    minWidth = parentData.size,
                    maxWidth = parentData.size,
                    minHeight = incomingConstraints.minHeight,
                    maxHeight = incomingConstraints.maxHeight
                )
                Direction.UP, Direction.DOWN -> Constraints(
                    minWidth = incomingConstraints.minWidth,
                    maxWidth = incomingConstraints.maxWidth,
                    minHeight = parentData.size,
                    maxHeight = parentData.size
                )
            }
            val placeable = measureable.measure(childConstraints)

            placeables += placeable
            parentDatas += parentData
        }

        layout(constraints.viewportCrossAxisSpace.round(), geometry.paintSize.round()) {
            val mainAxisUnit: PxPosition
            val crossAxisUnit: PxPosition
            val originOffset: PxPosition
            val addSize: Boolean

            when (constraints.mainAxisDirection.applyGrowthDirection(constraints.growthDirection)) {
                Direction.LEFT -> {
                    mainAxisUnit = PxPosition((-1).px, Px.Zero)
                    crossAxisUnit = PxPosition(Px.Zero, 1.px)
                    originOffset = PxPosition(geometry.paintSize, Px.Zero)
                    addSize = true
                }
                Direction.UP -> {
                    mainAxisUnit = PxPosition(Px.Zero, (-1).px)
                    crossAxisUnit = PxPosition(1.px, Px.Zero)
                    originOffset = PxPosition(Px.Zero, geometry.paintSize)
                    addSize = true
                }
                Direction.RIGHT -> {
                    mainAxisUnit = PxPosition(1.px, Px.Zero)
                    crossAxisUnit = PxPosition(Px.Zero, 1.px)
                    originOffset = PxPosition.Origin
                    addSize = false
                }
                Direction.DOWN -> {
                    mainAxisUnit = PxPosition(Px.Zero, 1.px)
                    crossAxisUnit = PxPosition(1.px, Px.Zero)
                    originOffset = PxPosition.Origin
                    addSize = false
                }
            }

            for (index in placeables.indices) {
                val placeable = placeables[index]
                val parentData = parentDatas[index]

                val mainAxisDelta = parentData.layoutOffset - constraints.scrollPosition
                val crossAxisDelta = Px.Zero
                var childOffset = PxPosition(
                    x = (originOffset.x.value + mainAxisUnit.x.value * mainAxisDelta.value + crossAxisUnit.x.value * crossAxisDelta.value).px,
                    y = (originOffset.y.value + mainAxisUnit.y.value * mainAxisDelta.value + crossAxisUnit.y.value * crossAxisDelta.value).px
                )

                val paintSize = when (constraints.mainAxisDirection) {
                    Direction.LEFT, Direction.RIGHT -> placeable.width
                    Direction.UP, Direction.DOWN -> placeable.height
                }

                if (addSize) {
                    childOffset = PxPosition(
                        x = childOffset.x + (mainAxisUnit.x.value * paintSize.value).px,
                        y = childOffset.y + (mainAxisUnit.y.value * paintSize.value).px
                    )
                }

                d { "child at $index main axis delta $mainAxisDelta child offset $childOffset paint size $paintSize remaining ${constraints.remainingPaintSpace}" }

                // If the child's visible interval (mainAxisDelta, mainAxisDelta + paintExtentOf(child))
                // does not intersect the paint extent interval (0, constraints.remainingPaintExtent), it's hidden.
                if (mainAxisDelta < constraints.remainingPaintSpace && mainAxisDelta + paintSize > Px.Zero) {
                    placeable.place(childOffset)
                }
            }
        }
    }
}*/



data class SliverChildParentData(
    override var index: Int,
    var layoutOffset: Px = Px.Zero
) : IndexedParentData

val LayoutNode.sliverChildParentData: SliverChildParentData get() = parentData as SliverChildParentData