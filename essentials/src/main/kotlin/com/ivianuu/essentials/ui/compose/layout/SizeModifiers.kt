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

package com.ivianuu.essentials.ui.compose.layout

import androidx.ui.core.AlignmentLine
import androidx.ui.core.Constraints
import androidx.ui.core.DensityScope
import androidx.ui.core.Dp
import androidx.ui.core.IntPx
import androidx.ui.core.IntPxPosition
import androidx.ui.core.IntPxSize
import androidx.ui.core.LayoutModifier
import androidx.ui.core.Measurable
import androidx.ui.core.coerceAtLeast
import androidx.ui.core.coerceAtMost
import androidx.ui.core.coerceIn
import androidx.ui.core.enforce

// todo remove once added to compose

fun Width(value: Dp): LayoutModifier = SizeModifier.Width(value)
fun MaxWidth(value: Dp): LayoutModifier = SizeModifier.MaxWidth(value)
fun MinWidth(value: Dp): LayoutModifier = SizeModifier.MinWidth(value)
fun Height(value: Dp): LayoutModifier = SizeModifier.Height(value)
fun MaxHeight(value: Dp): LayoutModifier = SizeModifier.MaxHeight(value)
fun MinHeight(value: Dp): LayoutModifier = SizeModifier.MinHeight(value)

private sealed class SizeModifier : LayoutModifier {
    class Width(private val value: Dp) : SizeModifier() {
        override fun DensityScope.modifyConstraints(constraints: Constraints): Constraints {
            return if (value != Dp.Infinity) {
                constraints
                    .copy(minWidth = value.toIntPx(), maxWidth = value.toIntPx())
                    .enforce(constraints)
            } else {
                constraints
            }
        }

        override fun DensityScope.minIntrinsicWidthOf(measurable: Measurable, height: IntPx) =
            if (value != Dp.Infinity) {
                value.toIntPx()
            } else {
                measurable.minIntrinsicWidth(height)
            }

        override fun DensityScope.maxIntrinsicWidthOf(measurable: Measurable, height: IntPx) =
            if (value != Dp.Infinity) {
                value.toIntPx()
            } else {
                measurable.maxIntrinsicWidth(height)
            }
    }

    class MaxWidth(private val value: Dp) : SizeModifier() {
        override fun DensityScope.modifyConstraints(constraints: Constraints): Constraints {
            return if (value != Dp.Infinity) {
                val w = value.toIntPx().coerceIn(constraints.minWidth, constraints.maxWidth)
                constraints.copy(maxWidth = w)
            } else {
                constraints
            }
        }

        override fun DensityScope.minIntrinsicWidthOf(measurable: Measurable, height: IntPx) =
            measurable.minIntrinsicWidth(height).also {
                if (value != Dp.Infinity) it.coerceAtMost(value.toIntPx())
            }

        override fun DensityScope.maxIntrinsicWidthOf(measurable: Measurable, height: IntPx) =
            measurable.maxIntrinsicWidth(height).also {
                if (value != Dp.Infinity) it.coerceAtMost(value.toIntPx())
            }
    }

    class MinWidth(private val value: Dp) : SizeModifier() {
        override fun DensityScope.modifyConstraints(constraints: Constraints): Constraints {
            return if (value != Dp.Infinity) {
                val w = value.toIntPx().coerceIn(constraints.minWidth, constraints.maxWidth)
                constraints.copy(minWidth = w)
            } else {
                constraints
            }
        }

        override fun DensityScope.minIntrinsicWidthOf(measurable: Measurable, height: IntPx) =
            measurable.minIntrinsicWidth(height).also {
                if (value != Dp.Infinity) it.coerceAtLeast(value.toIntPx())
            }

        override fun DensityScope.maxIntrinsicWidthOf(measurable: Measurable, height: IntPx) =
            measurable.maxIntrinsicWidth(height).also {
                if (value != Dp.Infinity) it.coerceAtLeast(value.toIntPx())
            }
    }

    class Height(private val value: Dp) : SizeModifier() {
        override fun DensityScope.modifyConstraints(constraints: Constraints): Constraints {
            return if (value != Dp.Infinity) {
                constraints
                    .copy(minHeight = value.toIntPx(), maxHeight = value.toIntPx())
                    .enforce(constraints)
            } else {
                constraints
            }
        }

        override fun DensityScope.minIntrinsicHeightOf(measurable: Measurable, width: IntPx) =
            if (value != Dp.Infinity) {
                value.toIntPx()
            } else {
                measurable.minIntrinsicHeight(width)
            }

        override fun DensityScope.maxIntrinsicHeightOf(measurable: Measurable, width: IntPx) =
            if (value != Dp.Infinity) {
                value.toIntPx()
            } else {
                measurable.maxIntrinsicHeight(width)
            }
    }

    class MaxHeight(private val value: Dp) : SizeModifier() {
        override fun DensityScope.modifyConstraints(constraints: Constraints): Constraints {
            return if (value != Dp.Infinity) {
                val h = value.toIntPx().coerceIn(constraints.minHeight, constraints.maxHeight)
                constraints.copy(maxHeight = h)
            } else {
                constraints
            }
        }

        override fun DensityScope.minIntrinsicHeightOf(measurable: Measurable, width: IntPx) =
            measurable.minIntrinsicHeight(width).also {
                if (value != Dp.Infinity) it.coerceAtMost(value.toIntPx())
            }

        override fun DensityScope.maxIntrinsicHeightOf(measurable: Measurable, width: IntPx) =
            measurable.maxIntrinsicHeight(width).also {
                if (value != Dp.Infinity) it.coerceAtMost(value.toIntPx())
            }
    }

    class MinHeight(private val value: Dp) : SizeModifier() {
        override fun DensityScope.modifyConstraints(constraints: Constraints): Constraints {
            return if (value != Dp.Infinity) {
                val h = value.toIntPx().coerceIn(constraints.minHeight, constraints.maxHeight)
                constraints.copy(minHeight = h)
            } else {
                constraints
            }
        }

        override fun DensityScope.minIntrinsicHeightOf(measurable: Measurable, width: IntPx) =
            measurable.minIntrinsicHeight(width).also {
                if (value != Dp.Infinity) it.coerceAtLeast(value.toIntPx())
            }

        override fun DensityScope.maxIntrinsicHeightOf(measurable: Measurable, width: IntPx) =
            measurable.maxIntrinsicHeight(width).also {
                if (value != Dp.Infinity) it.coerceAtLeast(value.toIntPx())
            }
    }

    abstract override fun DensityScope.modifyConstraints(constraints: Constraints): Constraints

    override fun DensityScope.modifySize(constraints: Constraints, childSize: IntPxSize):
            IntPxSize = childSize

    override fun DensityScope.minIntrinsicWidthOf(measurable: Measurable, height: IntPx): IntPx =
        measurable.minIntrinsicWidth(height)

    override fun DensityScope.maxIntrinsicWidthOf(measurable: Measurable, height: IntPx): IntPx =
        measurable.maxIntrinsicWidth(height)

    override fun DensityScope.minIntrinsicHeightOf(measurable: Measurable, width: IntPx): IntPx =
        measurable.minIntrinsicHeight(width)

    override fun DensityScope.maxIntrinsicHeightOf(measurable: Measurable, width: IntPx): IntPx =
        measurable.maxIntrinsicHeight(width)

    override fun DensityScope.modifyPosition(
        childPosition: IntPxPosition,
        childSize: IntPxSize,
        containerSize: IntPxSize
    ): IntPxPosition = childPosition

    override fun DensityScope.modifyAlignmentLine(line: AlignmentLine, value: IntPx?) = value

    override fun DensityScope.modifyParentData(parentData: Any?): Any? = parentData
}
