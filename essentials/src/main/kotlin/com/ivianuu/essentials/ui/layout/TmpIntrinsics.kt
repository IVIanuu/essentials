package com.ivianuu.essentials.ui.layout

import androidx.ui.core.AlignmentLine
import androidx.ui.core.Constraints
import androidx.ui.core.IntrinsicMeasurable
import androidx.ui.core.Layout
import androidx.ui.core.LayoutNode
import androidx.ui.core.Measurable
import androidx.ui.core.MeasureBlock
import androidx.ui.core.MeasureScope
import androidx.ui.core.Placeable
import androidx.ui.core.simpleIdentityToString
import androidx.ui.unit.Density
import androidx.ui.unit.IntPx
import androidx.ui.unit.IntPxPosition
import androidx.ui.unit.IntPxSize

/**
 * Identifies an [IntrinsicMeasurable] as a min or max intrinsic measurement.
 */
@PublishedApi
internal enum class IntrinsicMinMax {
    Min, Max
}

/**
 * Identifies an [IntrinsicMeasurable] as a width or height intrinsic measurement.
 */
@PublishedApi
internal enum class IntrinsicWidthHeight {
    Width, Height
}

/**
 * Used to return a fixed sized item for intrinsics measurements in [Layout]
 */
private class DummyPlaceable(width: IntPx, height: IntPx) : Placeable() {
    override fun get(line: AlignmentLine): IntPx? = null
    override val size = IntPxSize(width, height)
    override fun performPlace(position: IntPxPosition) {}
}

/**
 * A wrapper around a [Measurable] for intrinsic measurments in [Layout]. Consumers of
 * [Layout] don't identify intrinsic methods, but we can give a reasonable implementation
 * by using their [measure], substituting the intrinsics gathering method
 * for the [Measurable.measure] call.
 */
@PublishedApi
internal class DefaultIntrinsicMeasurable(
    val measurable: IntrinsicMeasurable,
    val minMax: IntrinsicMinMax,
    val widthHeight: IntrinsicWidthHeight
) : Measurable {
    override val parentData: Any?
        get() = measurable.parentData

    override fun measure(constraints: Constraints): Placeable {
        if (widthHeight == IntrinsicWidthHeight.Width) {
            val width = if (minMax == IntrinsicMinMax.Max) {
                measurable.maxIntrinsicWidth(constraints.maxHeight)
            } else {
                measurable.minIntrinsicWidth(constraints.maxHeight)
            }
            return DummyPlaceable(
                width,
                constraints.maxHeight
            )
        }
        val height = if (minMax == IntrinsicMinMax.Max) {
            measurable.maxIntrinsicHeight(constraints.maxWidth)
        } else {
            measurable.minIntrinsicHeight(constraints.maxWidth)
        }
        return DummyPlaceable(
            constraints.maxWidth,
            height
        )
    }

    override fun minIntrinsicWidth(height: IntPx): IntPx {
        return measurable.minIntrinsicWidth(height)
    }

    override fun maxIntrinsicWidth(height: IntPx): IntPx {
        return measurable.maxIntrinsicWidth(height)
    }

    override fun minIntrinsicHeight(width: IntPx): IntPx {
        return measurable.minIntrinsicHeight(width)
    }

    override fun maxIntrinsicHeight(width: IntPx): IntPx {
        return measurable.maxIntrinsicHeight(width)
    }
}

/**
 * Receiver scope for [Layout]'s layout lambda when used in an intrinsics call.
 */
@PublishedApi
internal class IntrinsicsMeasureScope(density: Density) : MeasureScope(), Density by density

/**
 * Default [LayoutNode.MeasureBlocks] object implementation, providing intrinsic measurements
 * that use the measure block replacing the measure calls with intrinsic measurement calls.
 */
fun MeasuringIntrinsicsMeasureBlocks(measureBlock: MeasureBlock) =
    object : LayoutNode.MeasureBlocks {
        override fun measure(
            measureScope: MeasureScope,
            measurables: List<Measurable>,
            constraints: Constraints
        ) = measureScope.measureBlock(measurables, constraints)

        override fun minIntrinsicWidth(
            density: Density,
            measurables: List<IntrinsicMeasurable>,
            h: IntPx
        ) = density.MeasuringMinIntrinsicWidth(measureBlock, measurables, h)

        override fun minIntrinsicHeight(
            density: Density,
            measurables: List<IntrinsicMeasurable>,
            w: IntPx
        ) = density.MeasuringMinIntrinsicHeight(measureBlock, measurables, w)

        override fun maxIntrinsicWidth(
            density: Density,
            measurables: List<IntrinsicMeasurable>,
            h: IntPx
        ) = density.MeasuringMaxIntrinsicWidth(measureBlock, measurables, h)

        override fun maxIntrinsicHeight(
            density: Density,
            measurables: List<IntrinsicMeasurable>,
            w: IntPx
        ) = density.MeasuringMaxIntrinsicHeight(measureBlock, measurables, w)

        override fun toString(): String {
            // this calls simpleIdentityToString on measureBlock because it is typically a lambda,
            // which has a useless toString that doesn't hint at the source location
            return simpleIdentityToString(
                this,
                "MeasuringIntrinsicsMeasureBlocks"
            ) + "{ measureBlock=${simpleIdentityToString(measureBlock)} }"
        }
    }

/**
 * Default implementation for the min intrinsic width of a layout. This works by running the
 * measure block with measure calls replaced with intrinsic measurement calls.
 */
private inline fun Density.MeasuringMinIntrinsicWidth(
    measureBlock: MeasureBlock,
    measurables: List<IntrinsicMeasurable>,
    h: IntPx
): IntPx {
    val mapped = measurables.map {
        DefaultIntrinsicMeasurable(
            it,
            IntrinsicMinMax.Min,
            IntrinsicWidthHeight.Width
        )
    }
    val constraints = Constraints(maxHeight = h)
    val layoutReceiver =
        IntrinsicsMeasureScope(this)
    val layoutResult = layoutReceiver.measureBlock(mapped, constraints)
    return layoutResult.width
}

/**
 * Default implementation for the min intrinsic width of a layout. This works by running the
 * measure block with measure calls replaced with intrinsic measurement calls.
 */
private inline fun Density.MeasuringMinIntrinsicHeight(
    measureBlock: MeasureBlock,
    measurables: List<IntrinsicMeasurable>,
    w: IntPx
): IntPx {
    val mapped = measurables.map {
        DefaultIntrinsicMeasurable(
            it,
            IntrinsicMinMax.Min,
            IntrinsicWidthHeight.Height
        )
    }
    val constraints = Constraints(maxWidth = w)
    val layoutReceiver =
        IntrinsicsMeasureScope(this)
    val layoutResult = layoutReceiver.measureBlock(mapped, constraints)
    return layoutResult.height
}

/**
 * Default implementation for the max intrinsic width of a layout. This works by running the
 * measure block with measure calls replaced with intrinsic measurement calls.
 */
private inline fun Density.MeasuringMaxIntrinsicWidth(
    measureBlock: MeasureBlock,
    measurables: List<IntrinsicMeasurable>,
    h: IntPx
): IntPx {
    val mapped = measurables.map {
        DefaultIntrinsicMeasurable(
            it,
            IntrinsicMinMax.Max,
            IntrinsicWidthHeight.Width
        )
    }
    val constraints = Constraints(maxHeight = h)
    val layoutReceiver =
        IntrinsicsMeasureScope(this)
    val layoutResult = layoutReceiver.measureBlock(mapped, constraints)
    return layoutResult.width
}

/**
 * Default implementation for the max intrinsic height of a layout. This works by running the
 * measure block with measure calls replaced with intrinsic measurement calls.
 */
private inline fun Density.MeasuringMaxIntrinsicHeight(
    measureBlock: MeasureBlock,
    measurables: List<IntrinsicMeasurable>,
    w: IntPx
): IntPx {
    val mapped = measurables.map {
        DefaultIntrinsicMeasurable(
            it,
            IntrinsicMinMax.Max,
            IntrinsicWidthHeight.Height
        )
    }
    val constraints = Constraints(maxWidth = w)
    val layoutReceiver =
        IntrinsicsMeasureScope(this)
    val layoutResult = layoutReceiver.measureBlock(mapped, constraints)
    return layoutResult.height
}
