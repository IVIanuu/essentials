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

import androidx.compose.Composable
import androidx.compose.memo
import androidx.compose.state
import androidx.compose.unaryPlus
import androidx.ui.core.Direction
import androidx.ui.core.IntPx
import androidx.ui.core.Layout
import androidx.ui.core.ParentData
import androidx.ui.core.Placeable
import androidx.ui.core.Px
import androidx.ui.core.PxPosition
import androidx.ui.core.PxSize
import androidx.ui.core.RepaintBoundary
import androidx.ui.core.ambientDensity
import androidx.ui.core.coerceIn
import androidx.ui.core.max
import androidx.ui.core.min
import androidx.ui.core.px
import androidx.ui.core.round
import com.github.ajalt.timberkt.d
import com.ivianuu.essentials.ui.compose.common.scrolling.ScrollPosition
import com.ivianuu.essentials.ui.compose.core.composable

// todo add visual overlow

@Composable
fun Viewport(
    position: ScrollPosition,
    center: Any? = null,
    mainAxisDirection: Direction = Direction.DOWN,
    crossAxisDirection: Direction = Direction.RIGHT,
    anchor: Float = 0f,
    children: SliverChildren.() -> Unit
) = composable("SliverLayout") {
    val state = +memo { ViewportState() }

    val density = +ambientDensity()
    val measureScope = +memo(density) { SliverMeasureScope(density) }
    state.measureScope = measureScope
    state.children = SliverChildren().apply(children).children
    state.position = position
    state.center = center
    state.mainAxisDirection = mainAxisDirection
    state.crossAxisDirection = crossAxisDirection
    state.anchor = anchor
    val (viewportSize, setViewportSize) = +state { PxSize.Zero }
    state.viewportSize = viewportSize

    state.performLayout()

    ViewportLayout(
        size = viewportSize,
        onSizeChanged = setViewportSize
    ) {
        state.childrenComposables.forEach { it() }
    }
}

private class ViewportState {

    lateinit var measureScope: SliverMeasureScope
    var children = emptyList<SliverChild>()
    lateinit var position: ScrollPosition
    var center: Any? = null
    var mainAxisDirection = Direction.DOWN
    var crossAxisDirection = Direction.RIGHT
    var anchor = 0f
    var viewportSize = PxSize.Zero

    val mainAxisSize: Px
        get() {
            return when (mainAxisDirection) {
                Direction.LEFT, Direction.RIGHT -> viewportSize.width
                Direction.UP, Direction.DOWN -> viewportSize.height
            }
        }
    val crossAxisSize: Px
        get() {
            return when (mainAxisDirection) {
                Direction.LEFT, Direction.RIGHT -> viewportSize.height
                Direction.UP, Direction.DOWN -> viewportSize.width
            }
        }

    private var minScrollPosition = Px.Zero
    private var maxScrollPosition = Px.Zero
    private var hasVisualOverflow = false

    val childrenComposables = mutableListOf<@Composable() () -> Unit>()

    fun performLayout() {
        if (viewportSize != PxSize.Zero) {
            if (children.isNotEmpty()) {
                var correction = Px.Zero
                val centerOffsetAdjustment = Px.Zero // todo implement
                var count = 0
                do {
                    correction = attemptLayout(
                        mainAxisSize,
                        crossAxisSize,
                        position.value + centerOffsetAdjustment
                    )
                    if (correction != Px.Zero) {
                        position.correctBy(correction)
                    } else {
                        val newMinValue = min(Px.Zero, minScrollPosition + mainAxisSize * anchor)
                        val newMaxValue =
                            max(Px.Zero, maxScrollPosition - mainAxisSize * (1f - anchor))
                        if (newMinValue != position.minValue || newMaxValue != position.maxValue) {
                            position.updateBounds(newMinValue, newMaxValue)
                            d { "update min max to ${position.minValue} ${position.maxValue}" }
                        }
                        break
                    }

                    count += 1;
                } while (count < 10)
                check(count < 10)
            } else {
                if (Px.Zero != position.maxValue || Px.Zero != position.minValue) {
                    position.updateBounds(Px.Zero, Px.Zero)
                }

            }
        }
    }

    private fun attemptLayout(mainAxisSize: Px, crossAxisSize: Px, correctedOffset: Px): Px {
        minScrollPosition = Px.Zero
        maxScrollPosition = Px.Zero
        hasVisualOverflow = false
        childrenComposables.clear()

        val centerOffset = mainAxisSize * anchor - correctedOffset
        val reverseDirectionRemainingPaintSpace = centerOffset.coerceIn(Px.Zero, mainAxisSize)
        val forwardDirectionRemainingPaintSpace =
            (mainAxisSize - centerOffset).coerceIn(Px.Zero, mainAxisSize)

        val cacheSize = defaultCacheSize // todo customize

        val fullCacheSpace = mainAxisSize + (cacheSize * 2)
        val centerCacheOffset = centerOffset + cacheSize
        val reverseDirectionRemainingCacheSpace =
            centerCacheOffset.coerceIn(Px.Zero, fullCacheSpace)
        val forwardDirectionRemainingCacheSpace =
            (fullCacheSpace - centerCacheOffset).coerceIn(Px.Zero, fullCacheSpace)

        d {
            "attempt layout:\n" +
                    "center offset $centerOffset\n" +
                    "remaining paint space reverse $reverseDirectionRemainingPaintSpace\n" +
                    "remaining paint space forward $forwardDirectionRemainingPaintSpace\n" +
                    "cache size $cacheSize\n" +
                    "full cache space $fullCacheSpace\n" +
                    "center cache offset $centerCacheOffset\n" +
                    "remaining cache space reverse $reverseDirectionRemainingCacheSpace\n" +
                    "remaining cache space forward $forwardDirectionRemainingCacheSpace"
        }

        val centerChild =
            if (center != null) children.first { it.key == center } else children.firstOrNull()
        val centerChildIndex = children.indexOf(centerChild)

        if (centerChildIndex > 0) {
            val result = layoutChildSequence(
                initialIndex = centerChildIndex - 1,
                scrollPosition = max(mainAxisSize, centerOffset) - mainAxisSize,
                overlap = Px.Zero,
                layoutOffset = forwardDirectionRemainingPaintSpace,
                remainingPaintSpace = reverseDirectionRemainingPaintSpace,
                mainAxisSize = mainAxisSize,
                crossAxisSize = crossAxisSize,
                growthDirection = GrowthDirection.Reverse,
                remainingCacheSpace = reverseDirectionRemainingCacheSpace,
                cacheOrigin = (mainAxisSize - centerOffset).coerceIn(-cacheSize, Px.Zero)
            )
            if (result != Px.Zero) return result
        }

        if (centerChild != null) {
            return layoutChildSequence(
                initialIndex = centerChildIndex,
                scrollPosition = max(Px.Zero, -centerOffset),
                overlap = if (centerChildIndex <= 0) min(
                    Px.Zero,
                    -centerOffset
                ) else Px.Zero,
                layoutOffset = if (centerOffset > mainAxisSize) centerOffset else reverseDirectionRemainingPaintSpace,
                remainingPaintSpace = forwardDirectionRemainingPaintSpace,
                mainAxisSize = mainAxisSize,
                crossAxisSize = crossAxisSize,
                growthDirection = GrowthDirection.Forward,
                remainingCacheSpace = forwardDirectionRemainingCacheSpace,
                cacheOrigin = centerOffset.coerceIn(-cacheSize, Px.Zero)
            )
        }

        return Px.Zero
    }

    private fun layoutChildSequence(
        initialIndex: Int,
        scrollPosition: Px,
        overlap: Px,
        layoutOffset: Px,
        remainingPaintSpace: Px,
        mainAxisSize: Px,
        crossAxisSize: Px,
        growthDirection: GrowthDirection,
        remainingCacheSpace: Px,
        cacheOrigin: Px
    ): Px {
        var scrollPosition = scrollPosition
        var layoutOffset = layoutOffset
        var remainingCacheSpace = remainingCacheSpace
        var cacheOrigin = cacheOrigin

        val initialLayoutOffset = layoutOffset
        val adjustedUserScrollDirection =
            position.direction.applyGrowthDirection(growthDirection)
        var maxPaintOffset = layoutOffset + overlap
        var precedingScrollSpace = Px.Zero

        d {
            "layout child sequence:\n" +
                    "initial index $initialIndex\n" +
                    "scroll position $scrollPosition\n" +
                    "overlap $overlap\n" +
                    "layout offset $layoutOffset\n" +
                    "initial layout offset $initialLayoutOffset\n" +
                    "adjusted user scroll direction $adjustedUserScrollDirection\n" +
                    "max paint offset $maxPaintOffset\n" +
                    "remainingPaintSpace $remainingPaintSpace\n" +
                    "main axis $mainAxisSize\n" +
                    "cross axis $crossAxisSize\n" +
                    "growth $growthDirection\n" +
                    "remaining cache $remainingCacheSpace\n" +
                    "cache origin $cacheOrigin"
        }

        var index = initialIndex
        var child: SliverChild? = children.getOrNull(index)
        while (child != null) {
            val sliverScrollPosition = if (scrollPosition <= Px.Zero) Px.Zero else scrollPosition
            val correctedCacheOrigin = max(cacheOrigin, -sliverScrollPosition)
            val cacheSpaceCorrection = cacheOrigin - correctedCacheOrigin

            d {
                "layout child at $index started\n" +
                        "sliver scroll position $sliverScrollPosition\n" +
                        "max paint offset $maxPaintOffset\n" +
                        "scroll position $scrollPosition\n" +
                        "preceding scroll space $precedingScrollSpace\n" +
                        "layout offset $layoutOffset\n" +
                        "remaining cache space $remainingCacheSpace\n" +
                        "cache origin $cacheOrigin\n" +
                        "min scroll $minScrollPosition\n" +
                        "max scroll $maxScrollPosition"
            }

            val constraints = SliverConstraints(
                mainAxisDirection = mainAxisDirection,
                growthDirection = growthDirection,
                userScrollDirection = adjustedUserScrollDirection,
                scrollPosition = sliverScrollPosition,
                precedingScrollSpace = precedingScrollSpace,
                overlap = maxPaintOffset - layoutOffset,
                remainingPaintSpace = max(
                    Px.Zero,
                    remainingPaintSpace - layoutOffset + initialLayoutOffset
                ),
                crossAxisSpace = crossAxisSize,
                crossAxisDirection = crossAxisDirection,
                viewportMainAxisSpace = mainAxisSize,
                remainingCacheSpace = max(Px.Zero, remainingCacheSpace + cacheSpaceCorrection),
                cacheOrigin = correctedCacheOrigin
            )

            val (geometry, childContent) = child.measureBlock(measureScope, constraints)
            geometry.checkGeometry()

            if (geometry.scrollOffsetCorrection != Px.Zero) return geometry.scrollOffsetCorrection

            val effectiveLayoutOffset = layoutOffset + geometry.paintOrigin
            val childLayoutOffset = if (geometry.visible || scrollPosition > Px.Zero) {
                effectiveLayoutOffset
            } else {
                -scrollPosition + initialLayoutOffset
            }

            maxPaintOffset = max(effectiveLayoutOffset + geometry.paintSize, maxPaintOffset)
            scrollPosition -= geometry.scrollSize
            precedingScrollSpace += geometry.scrollSize
            layoutOffset += geometry.layoutSize
            if (geometry.cacheSize != Px.Zero) {
                remainingCacheSpace -= geometry.cacheSize - cacheSpaceCorrection
                cacheOrigin = min(correctedCacheOrigin + geometry.cacheSize, Px.Zero)
            }

            val parentData = ViewportParentData(
                computeAbsolutePaintOffset(
                    geometry = geometry,
                    layoutOffset = childLayoutOffset,
                    viewportHeight = mainAxisSize,
                    viewportWidth = crossAxisSize,
                    mainAxisDirection = mainAxisDirection,
                    growthDirection = growthDirection
                ),
                geometry
            )

            childrenComposables += {
                composable(index) {
                    ParentData(data = parentData) {
                        RepaintBoundary {
                            childContent()
                        }
                    }
                }
            }

            when (growthDirection) {
                GrowthDirection.Forward -> maxScrollPosition += geometry.scrollSize
                GrowthDirection.Reverse -> minScrollPosition -= geometry.scrollSize
            }

            d {
                "layout child at $index finished\n" +
                        "geometry $geometry\n" +
                        "constraints $constraints\n" +
                        "position x ${parentData.position.x} y ${parentData.position.y}\n" +
                        "sliver scroll position $sliverScrollPosition\n" +
                        "effective layout offset $effectiveLayoutOffset\n" +
                        "child layout offset $childLayoutOffset\n" +
                        "max paint offset $maxPaintOffset\n" +
                        "scroll position $scrollPosition\n" +
                        "preceding scroll space $precedingScrollSpace\n" +
                        "layout offset $layoutOffset\n" +
                        "remaining cache space $remainingCacheSpace\n" +
                        "cache origin $cacheOrigin\n" +
                        "min scroll $minScrollPosition\n" +
                        "max scroll $maxScrollPosition"
            }

            index = when (growthDirection) {
                GrowthDirection.Forward -> index + 1
                GrowthDirection.Reverse -> index - 1
            }

            child = children.getOrNull(index)
        }

        return Px.Zero
    }

    private fun SliverGeometry.checkGeometry() {
        check(scrollSize >= Px.Zero)
        check(paintSize >= Px.Zero)
        check(layoutSize >= Px.Zero)
        check(cacheSize >= Px.Zero)
        check(layoutSize <= paintSize)
        check(paintSize <= maxPaintSize)
    }


    private fun computeAbsolutePaintOffset(
        geometry: SliverGeometry,
        layoutOffset: Px,
        viewportHeight: Px,
        viewportWidth: Px,
        mainAxisDirection: Direction,
        growthDirection: GrowthDirection
    ): PxPosition = when (mainAxisDirection.applyGrowthDirection(growthDirection)) {
        Direction.UP -> PxPosition(Px.Zero, viewportHeight - (layoutOffset + geometry.paintSize))
        Direction.RIGHT -> PxPosition(layoutOffset, Px.Zero)
        Direction.DOWN -> PxPosition(Px.Zero, layoutOffset)
        Direction.LEFT -> PxPosition(viewportWidth - (layoutOffset + geometry.paintSize), Px.Zero)
    }

}

private val defaultCacheSize = 250.px

@Composable
private fun ViewportLayout(
    size: PxSize,
    onSizeChanged: (PxSize) -> Unit,
    children: @Composable() () -> Unit
) = composable("ViewportLayout") {
    Layout(children = children) { measureables, constraints ->
        val placeables = mutableListOf<Placeable>()
        val parentDatas = mutableListOf<ViewportParentData>()

        for (i in measureables.indices) {
            val measureable = measureables[i]
            val parentData = measureable.parentData as ViewportParentData
            val childConstraints = constraints.copy(
                minWidth = constraints.maxWidth, // todo check
                minHeight = IntPx.Zero,
                maxHeight = parentData.geometry.scrollSize.round()
            )

            val placeable = measureable.measure(childConstraints)

            placeables += placeable
            parentDatas += parentData
        }

        layout(constraints.maxWidth, constraints.maxHeight) {
            val newSize = PxSize(constraints.maxWidth, constraints.maxHeight)
            if (size != newSize) onSizeChanged(newSize)

            for (i in placeables.indices) {
                val placeable = placeables[i]
                val parentData = parentDatas[i]
                if (parentData.geometry.visible) {
                    placeable.place(parentData.position.x, parentData.position.y)
                    d { "place child $parentData" }
                }
            }
        }
    }
}

private data class ViewportParentData(
    val position: PxPosition,
    val geometry: SliverGeometry
)