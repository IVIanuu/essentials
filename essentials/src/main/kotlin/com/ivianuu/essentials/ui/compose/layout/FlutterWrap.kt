/*
 * Copyright 2019 Manuel Wrage
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
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

package com.ivianuu.essentials.ui.compose.layout

import androidx.compose.Composable
import androidx.ui.core.Constraints
import androidx.ui.core.Dp
import androidx.ui.core.IntPx
import androidx.ui.core.Layout
import androidx.ui.core.LayoutDirection
import androidx.ui.core.Placeable
import androidx.ui.core.dp
import androidx.ui.core.max
import com.github.ajalt.timberkt.d
import com.ivianuu.essentials.ui.compose.core.Axis
import com.ivianuu.essentials.ui.compose.core.VerticalDirection
import com.ivianuu.essentials.ui.compose.core.composable
import com.ivianuu.essentials.ui.compose.core.withDensity


// todo find a good name

@Composable
fun FlutterWrap(
    direction: Axis = Axis.Horizontal,
    mainAxisAlignment: MainAxisAlignment = MainAxisAlignment.Start,
    spacing: Dp = 0.dp,
    runAlignment: MainAxisAlignment = MainAxisAlignment.Start,
    runSpacing: Dp = 0.dp,
    crossAxisAlignment: CrossAxisAlignment = CrossAxisAlignment.Start,
    horizontalDirection: LayoutDirection = LayoutDirection.Ltr,
    verticalDirection: VerticalDirection = VerticalDirection.Down,
    children: @Composable() () -> Unit
) = composable("FlutterWrap") {
    val spacingPx = withDensity { spacing.toIntPx() }
    val runSpacingPx = withDensity { runSpacing.toIntPx() }

    Layout(children = children) { measureables, constraints ->
        if (measureables.isEmpty()) return@Layout layout(
            constraints.minWidth,
            constraints.minHeight
        ) {}

        val childConstraints: Constraints
        val maxMainAxisSize: IntPx
        val flipMainAxis: Boolean
        val flipCrossAxis: Boolean

        when (direction) {
            Axis.Horizontal -> {
                childConstraints = Constraints(
                    minWidth = IntPx.Zero,
                    minHeight = IntPx.Zero,
                    maxWidth = constraints.maxWidth,
                    maxHeight = IntPx.Infinity
                )

                maxMainAxisSize = constraints.maxWidth
                flipMainAxis = horizontalDirection == LayoutDirection.Rtl
                flipCrossAxis = verticalDirection == VerticalDirection.Up
            }
            Axis.Vertical -> {
                childConstraints = Constraints(
                    minWidth = IntPx.Zero,
                    minHeight = IntPx.Zero,
                    maxWidth = IntPx.Infinity,
                    maxHeight = constraints.maxHeight
                )

                maxMainAxisSize = constraints.maxHeight
                flipMainAxis = verticalDirection == VerticalDirection.Up
                flipCrossAxis = horizontalDirection == LayoutDirection.Rtl
            }
        }

        var mainAxisSpace = IntPx.Zero
        var crossAxisSpace = IntPx.Zero

        val placeables = mutableListOf<Placeable>()
        val childPropertiesList = mutableListOf<ChildProperties>()

        val runMetrics = mutableListOf<RunMetrics>()

        var runMainAxisSpace = IntPx.Zero
        var runCrossAxisSpace = IntPx.Zero
        var childCount = 0

        for (i in measureables.indices) {
            val measureable = measureables[i]
            val placeable = measureable.measure(childConstraints)
            placeables += placeable

            val childMainAxisSize = placeable.mainAxisSize(direction)
            val childCrossAxisSize = placeable.crossAxisSize(direction)
            if (childCount > 0 && runMainAxisSpace + spacingPx + childMainAxisSize > maxMainAxisSize) {
                d { "next run" }
                mainAxisSpace = max(mainAxisSpace, runMainAxisSpace)
                crossAxisSpace += runCrossAxisSpace
                if (runMetrics.isNotEmpty()) crossAxisSpace += runSpacingPx
                runMetrics += RunMetrics(
                    runMainAxisSpace,
                    runCrossAxisSpace,
                    i - childCount,
                    childCount
                )
                runMainAxisSpace = IntPx.Zero
                runCrossAxisSpace = IntPx.Zero
                childCount = 0
            }

            runMainAxisSpace += childMainAxisSize
            if (childCount > 0) runMainAxisSpace += spacingPx
            runCrossAxisSpace = max(runCrossAxisSpace, childCrossAxisSize)
            ++childCount
            childPropertiesList += ChildProperties(runMetrics.size)
            d { "measured $measureable" }
        }

        if (childCount > 0) {
            mainAxisSpace = max(mainAxisSpace, runMainAxisSpace)
            crossAxisSpace += runCrossAxisSpace
            if (runMetrics.isNotEmpty()) crossAxisSpace += runSpacingPx
            runMetrics.add(
                RunMetrics(
                    runMainAxisSpace,
                    runCrossAxisSpace,
                    measureables.lastIndex - childCount,
                    childCount
                )
            )
        }

        val runCount = runMetrics.size

        d { "measured run main axis $runMainAxisSpace run cross $runCrossAxisSpace run count $runCount main axis $mainAxisSpace cross $crossAxisSpace" }

        val containerMainAxisSize: IntPx
        val containerCrossAxisSize: IntPx
        val width: IntPx
        val height: IntPx
        when (direction) {
            Axis.Horizontal -> {
                width = mainAxisSpace
                height = crossAxisSpace
                containerMainAxisSize = mainAxisSpace
                containerCrossAxisSize = crossAxisSpace
            }
            Axis.Vertical -> {
                width = crossAxisSpace
                height = mainAxisSpace
                containerMainAxisSize = mainAxisSpace
                containerCrossAxisSize = crossAxisSpace
            }
        }

        d { "layout w $width h $height" }

        layout(width, height) {
            val crossAxisFreeSpace = max(IntPx.Zero, containerCrossAxisSize)
            var runStartSpace = IntPx.Zero
            var runBetweenSpace = IntPx.Zero
            when (runAlignment) {
                MainAxisAlignment.Start -> {
                }
                MainAxisAlignment.End -> {
                    runStartSpace = crossAxisFreeSpace
                }
                MainAxisAlignment.Center -> {
                    runStartSpace = crossAxisFreeSpace / 2
                }
                MainAxisAlignment.SpaceBetween -> {
                    runBetweenSpace =
                        if (runCount > 1) crossAxisFreeSpace / (runCount - 1) else IntPx.Zero
                }
                MainAxisAlignment.SpaceAround -> {
                    runBetweenSpace = crossAxisFreeSpace / runCount
                    runStartSpace = runBetweenSpace / 2
                }
                MainAxisAlignment.SpaceEvenly -> {
                    runBetweenSpace = crossAxisSpace / (runCount + 1)
                    runStartSpace = runBetweenSpace
                }
            }
            runBetweenSpace += runSpacingPx

            var crossAxisPosition =
                if (flipCrossAxis) containerCrossAxisSize - runStartSpace else runStartSpace

            for (runIndex in runMetrics.indices) {
                val metrics = runMetrics[runIndex]
                val runMainAxisSize = metrics.mainAxisSize
                val runCrossAxisSize = metrics.crossAxisSize
                childCount = metrics.childCount

                d { "posi $runIndex $metrics" }

                val mainAxisFreeSpace = max(IntPx.Zero, containerMainAxisSize - runMainAxisSize)
                var childStartSpace = IntPx.Zero
                var childBetweenSpace = IntPx.Zero
                when (mainAxisAlignment) {
                    MainAxisAlignment.Start -> {
                    }
                    MainAxisAlignment.End -> {
                        childStartSpace = mainAxisFreeSpace
                    }
                    MainAxisAlignment.Center -> {
                        childStartSpace = mainAxisFreeSpace / 2
                    }
                    MainAxisAlignment.SpaceBetween -> {
                        childBetweenSpace =
                            if (childCount > 1) mainAxisFreeSpace / (childCount - 1) else IntPx.Zero
                    }
                    MainAxisAlignment.SpaceAround -> {
                        childBetweenSpace = mainAxisFreeSpace / childCount
                        childStartSpace = childBetweenSpace / 2
                    }
                    MainAxisAlignment.SpaceEvenly -> {
                        childBetweenSpace = mainAxisFreeSpace / (childCount + 1)
                        childStartSpace = childBetweenSpace
                    }
                }

                childBetweenSpace += spacingPx

                var childMainPosition =
                    if (flipMainAxis) containerMainAxisSize - childStartSpace else childStartSpace
                if (flipCrossAxis) crossAxisPosition -= runCrossAxisSize

                for (childIndex in placeables.indices) {
                    val childProperties = childPropertiesList[childIndex]
                    if (childProperties.runIndex != runIndex) {
                        d { "skip child $childIndex" }
                        continue
                    }

                    val placeable = placeables[childIndex]
                    val childMainAxisSize = placeable.mainAxisSize(direction)
                    val childCrossAxisSize = placeable.crossAxisSize(direction)
                    val childCrossAxisPosition = getChildCrossAxisPosition(
                        flipCrossAxis,
                        crossAxisAlignment,
                        runCrossAxisSize,
                        childCrossAxisSize
                    )

                    if (flipMainAxis) childMainPosition -= childMainAxisSize

                    val (childPositionX, childPositionY) = getPosition(
                        direction,
                        childMainPosition,
                        crossAxisPosition + childCrossAxisPosition
                    )
                    placeable.place(childPositionX, childPositionY)
                    d { "posi child $childIndex main size $childMainAxisSize cross size $childCrossAxisSize at x $childPositionX y $childPositionY main pos $childMainPosition cross $crossAxisPosition child cross $childCrossAxisPosition" }

                    if (flipMainAxis)
                        childMainPosition -= childBetweenSpace
                    else
                        childMainPosition += childMainAxisSize + childBetweenSpace
                }

                if (flipCrossAxis)
                    crossAxisPosition -= runBetweenSpace
                else
                    crossAxisPosition += runCrossAxisSize + runBetweenSpace
            }
        }
    }
}

private fun Placeable.mainAxisSize(direction: Axis) = when (direction) {
    Axis.Horizontal -> width
    Axis.Vertical -> height
}

private fun Placeable.crossAxisSize(direction: Axis) = when (direction) {
    Axis.Horizontal -> height
    Axis.Vertical -> width
}

private fun getChildCrossAxisPosition(
    flipCrossAxis: Boolean,
    crossAxisAlignment: CrossAxisAlignment,
    runCrossAxisSize: IntPx,
    childCrossAxisSize: IntPx
): IntPx {
    val freeSpace = runCrossAxisSize - childCrossAxisSize
    return when (crossAxisAlignment) {
        CrossAxisAlignment.Start -> if (flipCrossAxis) freeSpace else IntPx.Zero
        CrossAxisAlignment.End -> if (flipCrossAxis) IntPx.Zero else freeSpace
        CrossAxisAlignment.Center -> freeSpace / 2
    }
}

private fun getPosition(
    direction: Axis,
    mainAxisPosition: IntPx,
    crossAxisPosition: IntPx
): Pair<IntPx, IntPx> {
    return when (direction) {
        Axis.Horizontal -> mainAxisPosition to crossAxisPosition
        Axis.Vertical -> crossAxisPosition to mainAxisPosition
    }
}

private data class RunMetrics(
    val mainAxisSize: IntPx,
    val crossAxisSize: IntPx,
    val childStartIndex: Int,
    val childCount: Int
)

private data class ChildProperties(val runIndex: Int)

enum class MainAxisAlignment {
    Start,
    End,
    Center,
    SpaceBetween,
    SpaceAround,
    SpaceEvenly
}

enum class CrossAxisAlignment {
    Center, Start, End
}
