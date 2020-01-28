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

package com.ivianuu.essentials.ui.layout

import androidx.compose.Composable
import androidx.ui.core.Alignment
import androidx.ui.core.AlignmentLine
import androidx.ui.core.Constraints
import androidx.ui.core.HorizontalAlignmentLine
import androidx.ui.core.IntrinsicMeasurable
import androidx.ui.core.IntrinsicMeasureBlock
import androidx.ui.core.Layout
import androidx.ui.core.Modifier
import androidx.ui.core.ParentDataModifier
import androidx.ui.core.Placeable
import androidx.ui.core.VerticalAlignmentLine
import androidx.ui.layout.LayoutGravity
import androidx.ui.layout.LayoutScopeMarker
import androidx.ui.unit.Density
import androidx.ui.unit.IntPx
import androidx.ui.unit.IntPxSize
import androidx.ui.unit.ipx
import androidx.ui.unit.isFinite
import androidx.ui.unit.max
import androidx.ui.unit.px
import androidx.ui.unit.round
import androidx.ui.unit.toPx

// todo remove once we can use a LayoutModifier for crossAxisAlignment

/**
 * A FlexScope provides a scope for Inflexible/Flexible functions.
 */
@LayoutScopeMarker
sealed class FlexScope {
    /**
     * A layout modifier within a [Column] or [Row] that makes the target component flexible.
     * It will be assigned a space according to its flex weight relative to the flexible siblings.
     * When [tight] is set to true, the target component is forced to occupy the entire space
     * assigned to it by the parent. [LayoutFlexible] children will be measured after all the
     * [LayoutInflexible] ones have been measured, in order to divide the unclaimed space between
     * them.
     */
    fun LayoutFlexible(flex: Float, tight: Boolean = true): Modifier =
        if (tight) {
            FlexModifier(FlexChildProperties(flex, FlexFit.Tight))
        } else {
            FlexModifier(FlexChildProperties(flex, FlexFit.Loose))
        }

    /**
     * A layout modifier within a [Column] or [Row] that makes the target component inflexible.
     * All [LayoutInflexible] children will be measured before the [LayoutFlexible] ones. They will be
     * measured in the order they appear, without min constraints and with max constraints in
     * the main direction of the layout (maxHeight for Column and maxWidth for Row) such that
     * the sum of the space occupied by inflexible children will not exceed the incoming constraint
     * of the [Column] or [Row]: for example the first child of a [Column] will be measured with
     * maxHeight = column's maxHeight; the second child will be measured with maxHeight = column's
     * maxHeight - first child's height, and so on.
     */
    val LayoutInflexible: Modifier = inflexibleModifier

    internal companion object {
        val inflexibleModifier: Modifier = FlexModifier(
            FlexChildProperties(0f, FlexFit.Loose)
        )
    }

    /**
     * A layout modifier within a [Column] or [Row] that positions target component in a
     * perpendicular direction according to the [AlignmentLine] which is provided through the
     * [alignmentLineBlock].
     * If target component is the only component with the specified RelativeToSiblings modifier
     * within a Column or Row, then the component will be positioned using
     * [ColumnScope.Gravity.Start] in Column or [RowScope.Gravity.Top] in Row respectively.
     *
     * Example usage:
     * @sample androidx.ui.layout.samples.SimpleRelativeToSiblings
     */
    @Suppress("unused")
    fun LayoutGravity.RelativeToSiblings(alignmentLineBlock: (Placeable) -> IntPx): Modifier =
        SiblingsAlignedModifier.WithAlignmentLineBlock(alignmentLineBlock)
}

/**
 * A ColumnScope provides a scope for the children of a [Column].
 */
@Suppress("unused") // Note: Gravity object provides a scope only but is never used itself
class ColumnScope internal constructor() : FlexScope() {
    /**
     * A layout modifier within a Column that positions target component in a horizontal direction
     * so that its start edge is aligned to the start edge of the horizontal axis.
     */
    // TODO: Consider ltr/rtl.
    val LayoutGravity.Start: Modifier get() = StartGravityModifier
    /**
     * A layout modifier within a Column that positions target component in a horizontal direction
     * so that its center is in the middle of the horizontal axis.
     */
    val LayoutGravity.Center: Modifier get() = CenterGravityModifier
    /**
     * A layout modifier within a Column that positions target component in a horizontal direction
     * so that its end edge is aligned to the end edge of the horizontal axis.
     */
    val LayoutGravity.End: Modifier get() = EndGravityModifier

    /**
     * A layout modifier within a [Column] that positions target component in a perpendicular
     * direction according to the [AlignmentLine].
     * If target component is the only component within a Column with the specified
     * RelativeToSiblings modifier, or if the provided alignment line is not defined for the
     * component, the component will be positioned using [Gravity.Start].
     *
     * Example usage:
     *
     * @sample androidx.ui.layout.samples.SimpleRelativeToSiblingsInColumn
     */
    fun LayoutGravity.RelativeToSiblings(alignmentLine: VerticalAlignmentLine): Modifier =
        SiblingsAlignedModifier.WithAlignmentLine(alignmentLine)

    internal companion object {
        val StartGravityModifier: Modifier = GravityModifier(CrossAxisAlignment.Start)
        val CenterGravityModifier: Modifier = GravityModifier(CrossAxisAlignment.Center)
        val EndGravityModifier: Modifier = GravityModifier(CrossAxisAlignment.End)
    }
}

/**
 * A RowScope provides a scope for the children of a [Row].
 */
@Suppress("unused") // Note: Gravity object provides a scope only but is never used itself
class RowScope internal constructor() : FlexScope() {
    /**
     * A layout modifier within a Row that positions target component in a vertical direction
     * so that its top edge is aligned to the top edge of the vertical axis.
     */
    val LayoutGravity.Top: Modifier get() = TopGravityModifier
    /**
     * A layout modifier within a Row that positions target component in a vertical direction
     * so that its center is in the middle of the vertical axis.
     */
    val LayoutGravity.Center: Modifier get() = CenterGravityModifier
    /**
     * A layout modifier within a Row that positions target component in a vertical direction
     * so that its bottom edge is aligned to the bottom edge of the vertical axis.
     */
    val LayoutGravity.Bottom: Modifier get() = BottomGravityModifier

    /**
     * A layout modifier within a [Row] that positions target component in a perpendicular
     * direction according to the [AlignmentLine].
     * If target component is the only component within a Row with the specified
     * RelativeToSiblings modifier, or if the provided alignment line is not defined for the
     * component, the component will be positioned using [Gravity.Top].
     *
     * Example usage:
     * @sample androidx.ui.layout.samples.SimpleRelativeToSiblingsInRow
     */
    fun LayoutGravity.RelativeToSiblings(alignmentLine: HorizontalAlignmentLine): Modifier =
        SiblingsAlignedModifier.WithAlignmentLine(alignmentLine)

    internal companion object {
        val TopGravityModifier: Modifier = GravityModifier(CrossAxisAlignment.Start)
        val CenterGravityModifier: Modifier = GravityModifier(CrossAxisAlignment.Center)
        val BottomGravityModifier: Modifier = GravityModifier(CrossAxisAlignment.End)
    }
}

/**
 * A composable that places its children in a horizontal sequence and is able to assign them widths
 * according to their flex weights provided through [androidx.ui.layout.FlexScope.Flexible]
 * modifier.
 * If [androidx.ui.layout.FlexScope.Inflexible] or no modifier is provided, the child will be
 * treated as inflexible, and will be sized to its preferred width.
 *
 * Example usage:
 *
 * @sample androidx.ui.layout.samples.SimpleRow
 *
 * @param mainAxisAlignment The alignment of the layout's children in main axis direction.
 * Default is [MainAxisAlignment.Start].
 * @param crossAxisAlignment The alignment of the layout's children in cross axis direction.
 * Default is [CrossAxisAlignment.Start].
 */
@Composable
fun Row(
    modifier: Modifier = Modifier.None,
    mainAxisAlignment: MainAxisAlignment = MainAxisAlignment.Start,
    crossAxisAlignment: CrossAxisAlignment = CrossAxisAlignment.Start,
    children: @Composable() RowScope.() -> Unit
) {
    FlexLayout(
        orientation = LayoutOrientation.Horizontal,
        modifier = modifier,
        mainAxisAlignment = mainAxisAlignment,
        crossAxisAlignment = crossAxisAlignment,
        crossAxisSize = LayoutSize.Wrap,
        children = { RowScope().children() }
    )
}

/**
 * A composable that places its children in a vertical sequence and is able to assign them heights
 * according to their flex weights provided through [androidx.ui.layout.FlexScope.Flexible]
 * modifiers.
 * If [androidx.ui.layout.FlexScope.Inflexible] or no modifier is provided, the child will be
 * treated as inflexible, and will be sized to its preferred height.
 *
 * Example usage:
 *
 * @sample androidx.ui.layout.samples.SimpleColumn
 *
 * @param mainAxisAlignment The alignment of the layout's children in main axis direction.
 * Default is [MainAxisAlignment.Start].
 * @param crossAxisAlignment The alignment of the layout's children in cross axis direction.
 * Default is [CrossAxisAlignment.Start].
 */
@Composable
fun Column(
    modifier: Modifier = Modifier.None,
    mainAxisAlignment: MainAxisAlignment = MainAxisAlignment.Start,
    crossAxisAlignment: CrossAxisAlignment = CrossAxisAlignment.Start,
    children: @Composable() ColumnScope.() -> Unit
) {
    FlexLayout(
        orientation = LayoutOrientation.Vertical,
        modifier = modifier,
        mainAxisAlignment = mainAxisAlignment,
        crossAxisAlignment = crossAxisAlignment,
        crossAxisSize = LayoutSize.Wrap,
        children = { ColumnScope().children() }
    )
}

internal enum class FlexFit {
    Tight,
    Loose
}

internal enum class LayoutOrientation {
    Horizontal,
    Vertical
}

/**
 * Used to specify how a layout chooses its own size when multiple behaviors are possible.
 */
enum class LayoutSize {
    /**
     * Minimize the amount of free space by wrapping the children,
     * subject to the incoming layout constraints.
     */
    Wrap,
    /**
     * Maximize the amount of free space by expanding to fill the available space,
     * subject to the incoming layout constraints.
     */
    Expand
}

/**
 * Used to specify the alignment of a layout's children, in main axis direction.
 */
enum class MainAxisAlignment(internal val aligner: Aligner) {
    /**
     * Place children such that they are as close as possible to the middle of the main axis.
     */
    Center(MainAxisCenterAligner()),
    /**
     * Place children such that they are as close as possible to the start of the main axis.
     */
    // TODO(popam): Consider rtl directionality.
    Start(MainAxisStartAligner()),
    /**
     * Place children such that they are as close as possible to the end of the main axis.
     */
    End(MainAxisEndAligner()),
    /**
     * Place children such that they are spaced evenly across the main axis, including free
     * space before the first child and after the last child.
     */
    SpaceEvenly(MainAxisSpaceEvenlyAligner()),
    /**
     * Place children such that they are spaced evenly across the main axis, without free
     * space before the first child or after the last child.
     */
    SpaceBetween(MainAxisSpaceBetweenAligner()),
    /**
     * Place children such that they are spaced evenly across the main axis, including free
     * space before the first child and after the last child, but half the amount of space
     * existing otherwise between two consecutive children.
     */
    SpaceAround(MainAxisSpaceAroundAligner());

    internal interface Aligner {
        fun align(totalSize: IntPx, size: List<IntPx>): List<IntPx>
    }

    private class MainAxisCenterAligner : Aligner {
        override fun align(totalSize: IntPx, size: List<IntPx>): List<IntPx> {
            val consumedSize = size.fold(0.ipx) { a, b -> a + b }
            val positions = mutableListOf<IntPx>()
            var current = (totalSize - consumedSize).toPx() / 2
            size.forEach {
                positions.add(current.round())
                current += it
            }
            return positions
        }
    }

    private class MainAxisStartAligner : Aligner {
        override fun align(totalSize: IntPx, size: List<IntPx>): List<IntPx> {
            val positions = mutableListOf<IntPx>()
            var current = 0.ipx
            size.forEach {
                positions.add(current)
                current += it
            }
            return positions
        }
    }

    private class MainAxisEndAligner : Aligner {
        override fun align(totalSize: IntPx, size: List<IntPx>): List<IntPx> {
            val consumedSize = size.fold(0.ipx) { a, b -> a + b }
            val positions = mutableListOf<IntPx>()
            var current = totalSize - consumedSize
            size.forEach {
                positions.add(current)
                current += it
            }
            return positions
        }
    }

    private class MainAxisSpaceEvenlyAligner : Aligner {
        override fun align(totalSize: IntPx, size: List<IntPx>): List<IntPx> {
            val consumedSize = size.fold(0.ipx) { a, b -> a + b }
            val gapSize = (totalSize - consumedSize).toPx() / (size.size + 1)
            val positions = mutableListOf<IntPx>()
            var current = gapSize
            size.forEach {
                positions.add(current.round())
                current += it.toPx() + gapSize
            }
            return positions
        }
    }

    private class MainAxisSpaceBetweenAligner : Aligner {
        override fun align(totalSize: IntPx, size: List<IntPx>): List<IntPx> {
            val consumedSize = size.fold(0.ipx) { a, b -> a + b }
            val gapSize = if (size.size > 1) {
                (totalSize - consumedSize).toPx() / (size.size - 1)
            } else {
                0.px
            }
            val positions = mutableListOf<IntPx>()
            var current = 0.px
            size.forEach {
                positions.add(current.round())
                current += it.toPx() + gapSize
            }
            return positions
        }
    }

    private class MainAxisSpaceAroundAligner : Aligner {
        override fun align(totalSize: IntPx, size: List<IntPx>): List<IntPx> {
            val consumedSize = size.fold(0.ipx) { a, b -> a + b }
            val gapSize = if (size.isNotEmpty()) {
                (totalSize - consumedSize).toPx() / size.size
            } else {
                0.px
            }
            val positions = mutableListOf<IntPx>()
            var current = gapSize / 2
            size.forEach {
                positions.add(current.round())
                current += it.toPx() + gapSize
            }
            return positions
        }
    }
}

/**
 * Used to specify the alignment of a layout's children, in cross axis direction.
 */
// TODO(popam): refine this API surface with modifiers - add type safety for alignment orientation.
class CrossAxisAlignment private constructor(
    internal val alignmentLineProvider: AlignmentLineProvider? = null
) {
    companion object {
        /**
         * Place children such that their center is in the middle of the cross axis.
         */
        val Center = CrossAxisAlignment(null)
        /**
         * Place children such that their start edge is aligned to the start edge of the cross
         * axis. TODO(popam): Consider rtl directionality.
         */
        val Start = CrossAxisAlignment(null)
        /**
         * Place children such that their end edge is aligned to the end edge of the cross
         * axis. TODO(popam): Consider rtl directionality.
         */
        val End = CrossAxisAlignment(null)
        /**
         * Force children to occupy the entire cross axis space.
         */
        val Stretch = CrossAxisAlignment(null)

        /**
         * Align children by their baseline.
         */
        fun AlignmentLine(alignmentLine: AlignmentLine) =
            CrossAxisAlignment(AlignmentLineProvider.Value(alignmentLine))

        /**
         * Align children relative to their siblings using the alignment line provided as a
         * parameter using [AlignmentLineProvider].
         */
        internal fun Relative(alignmentLineProvider: AlignmentLineProvider) =
            CrossAxisAlignment(alignmentLineProvider)
    }
}

/**
 * Box [Constraints], but which abstract away width and height in favor of main axis and cross axis.
 */
internal data class OrientationIndependentConstraints(
    val mainAxisMin: IntPx,
    val mainAxisMax: IntPx,
    val crossAxisMin: IntPx,
    val crossAxisMax: IntPx
) {
    constructor(c: Constraints, orientation: LayoutOrientation) : this(
        if (orientation === LayoutOrientation.Horizontal) c.minWidth else c.minHeight,
        if (orientation === LayoutOrientation.Horizontal) c.maxWidth else c.maxHeight,
        if (orientation === LayoutOrientation.Horizontal) c.minHeight else c.minWidth,
        if (orientation === LayoutOrientation.Horizontal) c.maxHeight else c.maxWidth
    )

    // Creates a new instance with the same main axis constraints and maximum tight cross axis.
    fun stretchCrossAxis() = OrientationIndependentConstraints(
        mainAxisMin,
        mainAxisMax,
        if (crossAxisMax.isFinite()) crossAxisMax else crossAxisMin,
        crossAxisMax
    )

    // Given an orientation, resolves the current instance to traditional constraints.
    fun toBoxConstraints(orientation: LayoutOrientation) =
        if (orientation === LayoutOrientation.Horizontal) {
            Constraints(mainAxisMin, mainAxisMax, crossAxisMin, crossAxisMax)
        } else {
            Constraints(crossAxisMin, crossAxisMax, mainAxisMin, mainAxisMax)
        }

    // Given an orientation, resolves the max width constraint this instance represents.
    fun maxWidth(orientation: LayoutOrientation) =
        if (orientation === LayoutOrientation.Horizontal) {
            mainAxisMax
        } else {
            crossAxisMax
        }

    // Given an orientation, resolves the max height constraint this instance represents.
    fun maxHeight(orientation: LayoutOrientation) =
        if (orientation === LayoutOrientation.Horizontal) {
            crossAxisMax
        } else {
            mainAxisMax
        }
}

private val IntrinsicMeasurable.flex: Float
    get() = (parentData as? FlexChildProperties)?.flex ?: 0f

private val IntrinsicMeasurable.fit: FlexFit
    get() = (parentData as? FlexChildProperties)?.fit ?: FlexFit.Loose

private val IntrinsicMeasurable.crossAxisAlignment: CrossAxisAlignment?
    get() = (parentData as? FlexChildProperties)?.crossAxisAlignment

/**
 * Layout model that places its children in a horizontal or vertical sequence, according to the
 * specified orientation, while also looking at the flex weights of the children.
 */
@Composable
private fun FlexLayout(
    orientation: LayoutOrientation,
    modifier: Modifier = Modifier.None,
    mainAxisAlignment: MainAxisAlignment,
    crossAxisSize: LayoutSize,
    crossAxisAlignment: CrossAxisAlignment,
    children: @Composable () -> Unit
) {
    fun Placeable.mainAxisSize() =
        if (orientation == LayoutOrientation.Horizontal) width else height

    fun Placeable.crossAxisSize() =
        if (orientation == LayoutOrientation.Horizontal) height else width

    Layout(
        children,
        modifier = modifier,
        minIntrinsicWidthMeasureBlock = MinIntrinsicWidthMeasureBlock(orientation),
        minIntrinsicHeightMeasureBlock = MinIntrinsicHeightMeasureBlock(orientation),
        maxIntrinsicWidthMeasureBlock = MaxIntrinsicWidthMeasureBlock(orientation),
        maxIntrinsicHeightMeasureBlock = MaxIntrinsicHeightMeasureBlock(orientation)
    ) { measurables, outerConstraints ->
        val constraints = OrientationIndependentConstraints(outerConstraints, orientation)

        var totalFlex = 0f
        var inflexibleSpace = 0.ipx
        var crossAxisSpace = 0.ipx
        var beforeCrossAxisAlignmentLine = 0.ipx
        var afterCrossAxisAlignmentLine = 0.ipx

        val placeables = arrayOfNulls<Placeable>(measurables.size)
        // First measure children with zero flex.
        for (i in 0 until measurables.size) {
            val child = measurables[i]
            val flex = child.flex

            if (flex > 0f) {
                totalFlex += child.flex
            } else {
                val placeable = child.measure(
                    // Ask for preferred main axis size.
                    constraints.copy(
                        mainAxisMin = 0.ipx,
                        mainAxisMax = constraints.mainAxisMax - inflexibleSpace
                    ).let {
                        if (child.crossAxisAlignment == CrossAxisAlignment.Stretch ||
                            crossAxisAlignment == CrossAxisAlignment.Stretch
                        ) {
                            it.stretchCrossAxis()
                        } else {
                            it.copy(crossAxisMin = 0.ipx)
                        }
                    }.toBoxConstraints(orientation)
                )
                inflexibleSpace += placeable.mainAxisSize()
                crossAxisSpace = max(crossAxisSpace, placeable.crossAxisSize())

                val lineProvider = measurables[i].crossAxisAlignment?.alignmentLineProvider
                    ?: crossAxisAlignment.alignmentLineProvider
                if (lineProvider != null) {
                    val alignmentLinePosition = when (lineProvider) {
                        is AlignmentLineProvider.Block -> lineProvider.lineProviderBlock(placeable)
                        is AlignmentLineProvider.Value -> placeable[lineProvider.line]
                    }
                    beforeCrossAxisAlignmentLine = max(
                        beforeCrossAxisAlignmentLine,
                        alignmentLinePosition ?: 0.ipx
                    )
                    afterCrossAxisAlignmentLine = max(
                        afterCrossAxisAlignmentLine,
                        placeable.crossAxisSize() -
                                (alignmentLinePosition ?: placeable.crossAxisSize())
                    )
                }
                placeables[i] = placeable
            }
        }

        // Then measure the rest according to their flexes in the remaining main axis space.
        val targetSpace = if (totalFlex > 0f && constraints.mainAxisMax.isFinite()) {
            constraints.mainAxisMax
        } else {
            constraints.mainAxisMin
        }

        var flexibleSpace = 0.ipx

        for (i in 0 until measurables.size) {
            val child = measurables[i]
            val flex = child.flex
            if (flex > 0f) {
                val childMainAxisSize = max(
                    0.ipx,
                    (targetSpace - inflexibleSpace) * child.flex / totalFlex
                )
                val placeable = child.measure(
                    OrientationIndependentConstraints(
                        if (child.fit == FlexFit.Tight && childMainAxisSize.isFinite()) {
                            childMainAxisSize
                        } else {
                            0.ipx
                        },
                        childMainAxisSize,
                        if (child.crossAxisAlignment == CrossAxisAlignment.Stretch ||
                            crossAxisAlignment == CrossAxisAlignment.Stretch
                        ) {
                            constraints.crossAxisMax
                        } else {
                            0.ipx
                        },
                        constraints.crossAxisMax
                    ).toBoxConstraints(orientation)
                )
                flexibleSpace += placeable.mainAxisSize()
                crossAxisSpace = max(crossAxisSpace, placeable.crossAxisSize())
                placeables[i] = placeable
            }
        }

        // Compute the Flex size and position the children.
        val mainAxisLayoutSize = if (totalFlex > 0f && constraints.mainAxisMax.isFinite()) {
            constraints.mainAxisMax
        } else {
            max(inflexibleSpace + flexibleSpace, constraints.mainAxisMin)
        }
        val crossAxisLayoutSize = if (constraints.crossAxisMax.isFinite() &&
            crossAxisSize == LayoutSize.Expand
        ) {
            constraints.crossAxisMax
        } else {
            max(
                crossAxisSpace,
                max(
                    constraints.crossAxisMin,
                    beforeCrossAxisAlignmentLine + afterCrossAxisAlignmentLine
                )
            )
        }
        val layoutWidth = if (orientation == LayoutOrientation.Horizontal) {
            mainAxisLayoutSize
        } else {
            crossAxisLayoutSize
        }
        val layoutHeight = if (orientation == LayoutOrientation.Horizontal) {
            crossAxisLayoutSize
        } else {
            mainAxisLayoutSize
        }
        layout(layoutWidth, layoutHeight) {
            val childrenMainAxisSize = placeables.map { it!!.mainAxisSize() }
            val mainAxisPositions = mainAxisAlignment.aligner
                .align(mainAxisLayoutSize, childrenMainAxisSize)
            placeables.forEachIndexed { index, placeable ->
                placeable!!
                val childCrossAlignment =
                    measurables[index].crossAxisAlignment ?: crossAxisAlignment
                val crossAxis = when (childCrossAlignment) {
                    CrossAxisAlignment.Start -> 0.ipx
                    CrossAxisAlignment.Stretch -> 0.ipx
                    CrossAxisAlignment.End -> {
                        crossAxisLayoutSize - placeable.crossAxisSize()
                    }
                    CrossAxisAlignment.Center -> {
                        Alignment.Center.align(
                            IntPxSize(
                                mainAxisLayoutSize - placeable.mainAxisSize(),
                                crossAxisLayoutSize - placeable.crossAxisSize()
                            )
                        ).y
                    }
                    else -> {
                        val provider = measurables[index].crossAxisAlignment?.alignmentLineProvider
                            ?: crossAxisAlignment.alignmentLineProvider
                        val alignmentLinePosition = when (provider) {
                            is AlignmentLineProvider.Block -> provider.lineProviderBlock(placeable)
                            is AlignmentLineProvider.Value -> placeable[provider.line]
                            else -> null
                        }
                        if (alignmentLinePosition != null) {
                            beforeCrossAxisAlignmentLine - alignmentLinePosition
                        } else {
                            0.ipx
                        }
                    }
                }
                if (orientation == LayoutOrientation.Horizontal) {
                    placeable.place(mainAxisPositions[index], crossAxis)
                } else {
                    placeable.place(crossAxis, mainAxisPositions[index])
                }
            }
        }
    }
}

private /*inline*/ fun MinIntrinsicWidthMeasureBlock(orientation: LayoutOrientation) =
    if (orientation == LayoutOrientation.Horizontal) {
        IntrinsicMeasureBlocks.HorizontalMinWidth
    } else {
        IntrinsicMeasureBlocks.VerticalMinWidth
    }

private /*inline*/ fun MinIntrinsicHeightMeasureBlock(orientation: LayoutOrientation) =
    if (orientation == LayoutOrientation.Horizontal) {
        IntrinsicMeasureBlocks.HorizontalMinHeight
    } else {
        IntrinsicMeasureBlocks.VerticalMinHeight
    }

private /*inline*/ fun MaxIntrinsicWidthMeasureBlock(orientation: LayoutOrientation) =
    if (orientation == LayoutOrientation.Horizontal) {
        IntrinsicMeasureBlocks.HorizontalMaxWidth
    } else {
        IntrinsicMeasureBlocks.VerticalMaxWidth
    }

private /*inline*/ fun MaxIntrinsicHeightMeasureBlock(orientation: LayoutOrientation) =
    if (orientation == LayoutOrientation.Horizontal) {
        IntrinsicMeasureBlocks.HorizontalMaxHeight
    } else {
        IntrinsicMeasureBlocks.VerticalMaxHeight
    }

private object IntrinsicMeasureBlocks {
    val HorizontalMinWidth: IntrinsicMeasureBlock = { measurables, availableHeight ->
        intrinsicSize(
            measurables,
            { h -> minIntrinsicWidth(h) },
            { w -> maxIntrinsicHeight(w) },
            availableHeight,
            LayoutOrientation.Horizontal,
            LayoutOrientation.Horizontal
        )
    }
    val VerticalMinWidth: IntrinsicMeasureBlock = { measurables, availableHeight ->
        intrinsicSize(
            measurables,
            { h -> minIntrinsicWidth(h) },
            { w -> maxIntrinsicHeight(w) },
            availableHeight,
            LayoutOrientation.Vertical,
            LayoutOrientation.Horizontal
        )
    }
    val HorizontalMinHeight: IntrinsicMeasureBlock = { measurables, availableWidth ->
        intrinsicSize(
            measurables,
            { w -> minIntrinsicHeight(w) },
            { h -> maxIntrinsicWidth(h) },
            availableWidth,
            LayoutOrientation.Horizontal,
            LayoutOrientation.Vertical
        )
    }
    val VerticalMinHeight: IntrinsicMeasureBlock = { measurables, availableWidth ->
        intrinsicSize(
            measurables,
            { w -> minIntrinsicHeight(w) },
            { h -> maxIntrinsicWidth(h) },
            availableWidth,
            LayoutOrientation.Vertical,
            LayoutOrientation.Vertical
        )
    }
    val HorizontalMaxWidth: IntrinsicMeasureBlock = { measurables, availableHeight ->
        intrinsicSize(
            measurables,
            { h -> maxIntrinsicWidth(h) },
            { w -> maxIntrinsicHeight(w) },
            availableHeight,
            LayoutOrientation.Horizontal,
            LayoutOrientation.Horizontal
        )
    }
    val VerticalMaxWidth: IntrinsicMeasureBlock = { measurables, availableHeight ->
        intrinsicSize(
            measurables,
            { h -> maxIntrinsicWidth(h) },
            { w -> maxIntrinsicHeight(w) },
            availableHeight,
            LayoutOrientation.Vertical,
            LayoutOrientation.Horizontal
        )
    }
    val HorizontalMaxHeight: IntrinsicMeasureBlock = { measurables, availableWidth ->
        intrinsicSize(
            measurables,
            { w -> maxIntrinsicHeight(w) },
            { h -> maxIntrinsicWidth(h) },
            availableWidth,
            LayoutOrientation.Horizontal,
            LayoutOrientation.Vertical
        )
    }
    val VerticalMaxHeight: IntrinsicMeasureBlock = { measurables, availableWidth ->
        intrinsicSize(
            measurables,
            { w -> maxIntrinsicHeight(w) },
            { h -> maxIntrinsicWidth(h) },
            availableWidth,
            LayoutOrientation.Vertical,
            LayoutOrientation.Vertical
        )
    }
}

private fun intrinsicSize(
    children: List<IntrinsicMeasurable>,
    intrinsicMainSize: IntrinsicMeasurable.(IntPx) -> IntPx,
    intrinsicCrossSize: IntrinsicMeasurable.(IntPx) -> IntPx,
    crossAxisAvailable: IntPx,
    flexOrientation: LayoutOrientation,
    intrinsicOrientation: LayoutOrientation
) = if (flexOrientation == intrinsicOrientation) {
    intrinsicMainAxisSize(children, intrinsicMainSize, crossAxisAvailable)
} else {
    intrinsicCrossAxisSize(children, intrinsicCrossSize, intrinsicMainSize, crossAxisAvailable)
}

private fun intrinsicMainAxisSize(
    children: List<IntrinsicMeasurable>,
    mainAxisSize: IntrinsicMeasurable.(IntPx) -> IntPx,
    crossAxisAvailable: IntPx
): IntPx {
    var maxFlexibleSpace = 0.ipx
    var inflexibleSpace = 0.ipx
    var totalFlex = 0f
    children.forEach { child ->
        val flex = child.flex
        val size = child.mainAxisSize(crossAxisAvailable)
        if (flex == 0f) {
            inflexibleSpace += size
        } else if (flex > 0f) {
            totalFlex += flex
            maxFlexibleSpace = max(maxFlexibleSpace, size / flex)
        }
    }
    return maxFlexibleSpace * totalFlex + inflexibleSpace
}

private fun intrinsicCrossAxisSize(
    children: List<IntrinsicMeasurable>,
    mainAxisSize: IntrinsicMeasurable.(IntPx) -> IntPx,
    crossAxisSize: IntrinsicMeasurable.(IntPx) -> IntPx,
    mainAxisAvailable: IntPx
): IntPx {
    var inflexibleSpace = 0.ipx
    var crossAxisMax = 0.ipx
    var totalFlex = 0f
    children.forEach { child ->
        val flex = child.flex
        if (flex == 0f) {
            val mainAxisSpace = child.mainAxisSize(IntPx.Infinity)
            inflexibleSpace += mainAxisSpace
            crossAxisMax = max(crossAxisMax, child.crossAxisSize(mainAxisSpace))
        } else if (flex > 0f) {
            totalFlex += flex
        }
    }

    val flexSection = if (totalFlex == 0f) {
        0.ipx
    } else {
        max(mainAxisAvailable - inflexibleSpace, 0.ipx) / totalFlex
    }

    children.forEach { child ->
        if (child.flex > 0f) {
            crossAxisMax = max(crossAxisMax, child.crossAxisSize(flexSection * child.flex))
        }
    }
    return crossAxisMax
}

private data class FlexModifier(val flexProperties: FlexChildProperties) : ParentDataModifier {
    override fun Density.modifyParentData(parentData: Any?): FlexChildProperties {
        return ((parentData as? FlexChildProperties) ?: FlexChildProperties()).also {
            it.flex = flexProperties.flex
            it.fit = flexProperties.fit
        }
    }
}

private sealed class SiblingsAlignedModifier : ParentDataModifier {
    internal data class WithAlignmentLineBlock(val block: (Placeable) -> IntPx) :
        SiblingsAlignedModifier() {
        override fun Density.modifyParentData(parentData: Any?): Any? {
            return ((parentData as? FlexChildProperties) ?: FlexChildProperties()).also {
                it.crossAxisAlignment =
                    CrossAxisAlignment.Relative(AlignmentLineProvider.Block(block))
            }
        }
    }

    internal data class WithAlignmentLine(val line: AlignmentLine) :
        SiblingsAlignedModifier() {
        override fun Density.modifyParentData(parentData: Any?): Any? {
            return ((parentData as? FlexChildProperties) ?: FlexChildProperties()).also {
                it.crossAxisAlignment =
                    CrossAxisAlignment.Relative(AlignmentLineProvider.Value(line))
            }
        }
    }
}

private data class GravityModifier(val alignment: CrossAxisAlignment) : ParentDataModifier {
    override fun Density.modifyParentData(parentData: Any?): FlexChildProperties {
        return ((parentData as? FlexChildProperties) ?: FlexChildProperties()).also {
            it.crossAxisAlignment = alignment
        }
    }
}

/**
 * Parent data associated with children.
 */
private data class FlexChildProperties(
    var flex: Float? = null,
    var fit: FlexFit? = null,
    var crossAxisAlignment: CrossAxisAlignment? = null
)

/**
 * Provides the alignment line.
 */
internal sealed class AlignmentLineProvider {
    data class Block(val lineProviderBlock: (Placeable) -> IntPx) : AlignmentLineProvider()
    data class Value(val line: AlignmentLine) : AlignmentLineProvider()
}
