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
import androidx.ui.core.Density
import androidx.ui.core.DensityScope
import androidx.ui.core.Direction
import androidx.ui.core.Layout
import androidx.ui.core.ParentData
import androidx.ui.core.Placeable
import androidx.ui.core.Px
import androidx.ui.core.PxPosition
import androidx.ui.core.RepaintBoundary
import androidx.ui.core.ambientDensity
import androidx.ui.core.coerceIn
import androidx.ui.core.max
import androidx.ui.core.min
import androidx.ui.core.px
import androidx.ui.core.round
import androidx.ui.core.toPx
import com.github.ajalt.timberkt.d
import com.ivianuu.essentials.ui.compose.core.composable

// todo add visual overlow


@Composable
fun Viewport(
    position: SliverScrollPosition,
    center: Any? = null,
    mainAxisDirection: Direction = Direction.DOWN,
    crossAxisDirection: Direction = Direction.LEFT,
    anchor: Float = 0f,
    children: SliverChildren.() -> Unit
) = composable("SliverLayout") {
    val sliverChildren = +memo(children) {
        SliverChildren().apply(children).children
    }

    val (mainAxisSize, setMainAxisSize) = +state { (-1).px }
    val (crossAxisSize, setCrossAxisSize) = +state { (-1).px }

    val density = +ambientDensity()
    val measureScope = +memo(density) { SliverMeasureScope(density) }

    val childrenBlocks = mutableListOf<@Composable() () -> Unit>()

    fun childBefore(child: SliverChild?): SliverChild? {
        val index = sliverChildren.indexOf(child) - 1
        return sliverChildren.getOrNull(index)
    }

    fun childAfter(child: SliverChild?): SliverChild? {
        val index = sliverChildren.indexOf(child) + 1
        return sliverChildren.getOrNull(index)
    }

    var minScrollPosition = Px.Zero
    var maxScrollPosition = Px.Zero
    var hasVisualOverflow = false

    fun layoutChildSequence(
        child: SliverChild,
        scrollPosition: Px,
        overlap: Px,
        layoutOffset: Px,
        remainingPaintSpace: Px,
        mainAxisSize: Px,
        crossAxisSize: Px,
        growthDirection: GrowthDirection,
        advance: (SliverChild) -> SliverChild?,
        remainingCacheSpace: Px,
        cacheOrigin: Px
    ): Px {
        d {
            "layout child sequence " +
                    "scroll position $scrollPosition " +
                    "overlap $overlap " +
                    "layout offset $layoutOffset " +
                    "remainingPaintSpace $remainingPaintSpace " +
                    "main axis $mainAxisSize " +
                    "cross axis $crossAxisSize " +
                    "growth $growthDirection " +
                    "remaining cache $remainingCacheSpace " +
                    "cache origin $cacheOrigin"
        }
        var scrollPosition = scrollPosition
        var layoutOffset = layoutOffset
        var remainingCacheSpace = remainingCacheSpace
        var cacheOrigin = cacheOrigin

        val initialLayoutOffset = layoutOffset
        val adjustedUserScrollDirection =
            applyGrowthDirectionToScrollDirection(position.direction, growthDirection)
        var maxPaintOffset = layoutOffset + overlap
        var precedingScrollSpace = Px.Zero

        d { "adjusted user scroll direction $adjustedUserScrollDirection" }

        var child: SliverChild? = child
        while (child != null) {
            val sliverScrollPosition = if (scrollPosition <= Px.Zero) Px.Zero else scrollPosition
            val correctedCacheOrigin = max(cacheOrigin, -sliverScrollPosition)
            val cacheSpaceCorrection = cacheOrigin - correctedCacheOrigin

            d { "layout child pos $sliverScrollPosition orig $scrollPosition" }

            val (geometry, content) = child.measureBlock(
                measureScope, SliverConstraints(
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
            )

            if (geometry.scrollOffsetCorrection != Px.Zero) return geometry.scrollOffsetCorrection

            val effectiveLayoutOffset = layoutOffset + geometry.paintOrigin
            val childLayoutOffset = if (geometry.visible || scrollPosition > Px.Zero) {
                effectiveLayoutOffset
            } else {
                -scrollPosition + initialLayoutOffset
            }

            d { "effective layout offset $effectiveLayoutOffset child layout offset $childLayoutOffset" }

            maxPaintOffset = max(effectiveLayoutOffset + geometry.paintSize, maxPaintOffset)
            scrollPosition -= geometry.scrollSize
            precedingScrollSpace += geometry.scrollSize
            layoutOffset += geometry.layoutSize
            if (geometry.cacheSize != Px.Zero) {
                remainingCacheSpace -= geometry.cacheSize - cacheSpaceCorrection
                cacheOrigin = min(correctedCacheOrigin + geometry.cacheSize, Px.Zero)
            }

            d {
                "max paint offset $maxPaintOffset " +
                        "scroll position $scrollPosition " +
                        "preceding scroll space $precedingScrollSpace " +
                        "layout offset $layoutOffset " +
                        "remaining cache space $remainingCacheSpace " +
                        "cache origin $cacheOrigin"
            }

            childrenBlocks += {
                val parentData = SliverParentData(
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
                d { "parent data is ${parentData.position.x} ${parentData.position.y} ${parentData.geometry}" }
                // todo
                ParentData(data = parentData) {
                    RepaintBoundary {
                        content()
                    }
                }
            }

            when (growthDirection) {
                GrowthDirection.Forward -> maxScrollPosition += geometry.scrollSize
                GrowthDirection.Reverse -> minScrollPosition -= geometry.scrollSize
            }

            child = advance(child)
        }

        return Px.Zero
    }

    fun attemptLayout(mainAxisSize: Px, crossAxisSize: Px, correctedOffset: Px): Px {
        minScrollPosition = Px.Zero
        maxScrollPosition = Px.Zero
        hasVisualOverflow = false

        val centerOffset = mainAxisSize * anchor - correctedOffset
        val reverseDirectionRemainingPaintSpace = centerOffset.coerceIn(Px.Zero, mainAxisSize)
        val forwardDirectionRemainingPaintSpace =
            (mainAxisSize - centerOffset).coerceIn(Px.Zero, mainAxisSize)

        d { "attempt layout center offset $centerOffset paint space r $reverseDirectionRemainingPaintSpace paint space f $forwardDirectionRemainingPaintSpace " }

        val cacheSize = defaultCacheSize // todo customize

        val fullCacheSpace = mainAxisSize + (cacheSize * 2)
        val centerCacheOffset = centerOffset + cacheSize
        val reverseDirectionRemainingCacheSpace =
            centerCacheOffset.coerceIn(Px.Zero, fullCacheSpace)
        val forwardDirectionRemainingCacheSpace =
            (fullCacheSpace - centerCacheOffset).coerceIn(Px.Zero, fullCacheSpace)

        val centerChild =
            if (center != null) sliverChildren.first { it.key == center } else sliverChildren.firstOrNull()

        val leadingNegativeChild = childBefore(centerChild)
        if (leadingNegativeChild != null) {
            val result = layoutChildSequence(
                child = leadingNegativeChild,
                scrollPosition = max(mainAxisSize, centerOffset) - mainAxisSize,
                overlap = Px.Zero,
                layoutOffset = forwardDirectionRemainingPaintSpace,
                remainingPaintSpace = reverseDirectionRemainingPaintSpace,
                mainAxisSize = mainAxisSize,
                crossAxisSize = crossAxisSize,
                growthDirection = GrowthDirection.Reverse,
                advance = ::childBefore,
                remainingCacheSpace = reverseDirectionRemainingCacheSpace,
                cacheOrigin = (mainAxisSize - centerOffset).coerceIn(-cacheSize, Px.Zero)
            )
            if (result != Px.Zero) return result
        }

        if (centerChild != null) {
            return layoutChildSequence(
                child = centerChild,
                scrollPosition = max(Px.Zero, -centerOffset),
                overlap = if (leadingNegativeChild == null) min(
                    Px.Zero,
                    -centerOffset
                ) else Px.Zero,
                layoutOffset = if (centerOffset > mainAxisSize) centerOffset else reverseDirectionRemainingPaintSpace,
                remainingPaintSpace = forwardDirectionRemainingPaintSpace,
                mainAxisSize = mainAxisSize,
                crossAxisSize = crossAxisSize,
                growthDirection = GrowthDirection.Forward,
                advance = ::childAfter,
                remainingCacheSpace = forwardDirectionRemainingCacheSpace,
                cacheOrigin = centerOffset.coerceIn(-cacheSize, Px.Zero)
            )
        }

        return Px.Zero
    }

    d { "scroll position ${-position.value} direction ${position.direction}" }

    if (mainAxisSize != (-1).px && crossAxisSize != (-1).px) {
        if (sliverChildren.isNotEmpty()) {
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
                    val newMaxValue = max(Px.Zero, maxScrollPosition - mainAxisSize * (1f - anchor))
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
                d { "disable scroll" }
                position.updateBounds(Px.Zero, Px.Zero)
            }

        }
    }

    ViewportLayout(
        mainAxisDirection = mainAxisDirection,
        mainAxisSize = mainAxisSize,
        crossAxisSize = crossAxisSize,
        onSizeChanged = { newMainAxisSize, newCrossAxisSize ->
            setMainAxisSize(newMainAxisSize)
            setCrossAxisSize(newCrossAxisSize)
        }
    ) {
        childrenBlocks.forEach { it() }
    }
}

private fun applyGrowthDirectionToScrollDirection(
    scrollDirection: ScrollDirection,
    growthDirection: GrowthDirection
): ScrollDirection {
    return when (growthDirection) {
        GrowthDirection.Forward -> scrollDirection
        GrowthDirection.Reverse -> flipScrollDirection(scrollDirection)
    }
}

private fun applyGrowthDirectionToAxisDirection(
    axisDirection: Direction,
    growthDirection: GrowthDirection
): Direction {
    return when (growthDirection) {
        GrowthDirection.Forward -> axisDirection
        GrowthDirection.Reverse -> flipAxisDirection(axisDirection)
    }
}

private fun flipAxisDirection(axisDirection: Direction): Direction {
    return when (axisDirection) {
        Direction.UP -> Direction.DOWN
        Direction.RIGHT -> Direction.LEFT
        Direction.DOWN -> Direction.UP
        Direction.LEFT -> Direction.RIGHT
    }
}

private fun flipScrollDirection(direction: ScrollDirection): ScrollDirection {
    return when (direction) {
        ScrollDirection.Idle -> ScrollDirection.Idle
        ScrollDirection.Forward -> ScrollDirection.Reverse
        ScrollDirection.Reverse -> ScrollDirection.Forward
    }
}


private fun computeAbsolutePaintOffset(
    geometry: SliverGeometry,
    layoutOffset: Px,
    viewportHeight: Px,
    viewportWidth: Px,
    mainAxisDirection: Direction,
    growthDirection: GrowthDirection
): PxPosition {
    return when (applyGrowthDirectionToAxisDirection(mainAxisDirection, growthDirection)) {
        Direction.UP -> PxPosition(Px.Zero, viewportHeight - (layoutOffset + geometry.paintSize))
        Direction.RIGHT -> PxPosition(layoutOffset, Px.Zero)
        Direction.DOWN -> PxPosition(Px.Zero, layoutOffset)
        Direction.LEFT -> PxPosition(viewportWidth - (layoutOffset + geometry.paintSize), Px.Zero)
    }
}

private val defaultCacheSize = 250.px

@Composable
private fun ViewportLayout(
    mainAxisDirection: Direction,
    mainAxisSize: Px,
    crossAxisSize: Px,
    onSizeChanged: (Px, Px) -> Unit,
    children: @Composable() () -> Unit
) = composable("ViewportLayout") {
    Layout(children = children) { measureables, constraints ->
        val placeables = mutableListOf<Placeable>()
        val parentDatas = mutableListOf<SliverParentData>()

        for (i in measureables.indices) {
            val measureable = measureables[i]
            val parentData = measureable.parentData as SliverParentData
            val childConstraints = constraints.copy(
                minWidth = constraints.maxWidth, // todo check
                minHeight = parentData.geometry.scrollSize.round(),
                maxHeight = parentData.geometry.scrollSize.round()
            )

            val placeable = measureable.measure(childConstraints)

            placeables += placeable
            parentDatas += parentData
        }

        layout(constraints.maxWidth, constraints.maxHeight) {
            val newMainAxisSize = when (mainAxisDirection) {
                Direction.LEFT, Direction.RIGHT -> constraints.maxWidth.toPx()
                Direction.UP, Direction.DOWN -> constraints.maxHeight.toPx()
            }
            val newCrossAxisSize = when (mainAxisDirection) {
                Direction.LEFT, Direction.RIGHT -> constraints.maxHeight.toPx()
                Direction.UP, Direction.DOWN -> constraints.maxWidth.toPx()
            }

            if (newMainAxisSize != mainAxisSize || newCrossAxisSize != crossAxisSize) {
                onSizeChanged(newMainAxisSize, newCrossAxisSize)
            }

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

class SliverChildren {

    internal val children = mutableListOf<SliverChild>()

    fun Sliver(key: Any? = null, measureBlock: SliverMeasureBlock) {
        children += SliverChild(key, measureBlock)
    }

}

data class SliverConstraints(
    val mainAxisDirection: Direction,
    val growthDirection: GrowthDirection,
    val userScrollDirection: ScrollDirection,
    val scrollPosition: Px,
    val precedingScrollSpace: Px,
    val overlap: Px,
    val remainingPaintSpace: Px,
    val crossAxisSpace: Px,
    val crossAxisDirection: Direction,
    val viewportMainAxisSpace: Px,
    val remainingCacheSpace: Px,
    val cacheOrigin: Px
)

data class SliverGeometry(
    val scrollSize: Px = Px.Zero,
    val paintSize: Px = Px.Zero,
    val paintOrigin: Px = Px.Zero,
    val layoutSize: Px = paintSize,
    val maxPaintSize: Px = Px.Zero,
    val maxScrollObstructionSize: Px = Px.Zero,
    val visible: Boolean = paintSize > Px.Zero,
    val hasVisualOverflow: Boolean = false,
    val scrollOffsetCorrection: Px = Px.Zero,
    val cacheSize: Px = layoutSize
)

enum class GrowthDirection {
    Forward, Reverse
}

internal data class SliverChild(
    val key: Any?,
    val measureBlock: SliverMeasureBlock
)

class SliverMeasureScope(override val density: Density) : DensityScope {
    fun content(
        geometry: SliverGeometry,
        content: @Composable() () -> Unit
    ): SliverMeasureResult = SliverMeasureResult(geometry, content)
}

typealias SliverMeasureBlock = SliverMeasureScope.(constraints: SliverConstraints) -> SliverMeasureResult

data class SliverMeasureResult internal constructor(
    val geometry: SliverGeometry,
    val content: @Composable() () -> Unit
)

private data class SliverParentData(
    val position: PxPosition,
    val geometry: SliverGeometry
)