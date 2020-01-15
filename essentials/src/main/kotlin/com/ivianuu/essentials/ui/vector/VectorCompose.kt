package com.ivianuu.essentials.ui.vector

import androidx.compose.Composable
import androidx.compose.compositionReference
import androidx.compose.onPreCommit
import androidx.compose.remember
import androidx.ui.core.Alignment
import androidx.ui.core.Dp
import androidx.ui.core.Draw
import androidx.ui.core.IntPx
import androidx.ui.core.IntPxSize
import androidx.ui.core.Px
import androidx.ui.core.PxSize
import androidx.ui.graphics.BlendMode
import androidx.ui.graphics.Brush
import androidx.ui.graphics.Color
import androidx.ui.graphics.ScaleFit
import androidx.ui.graphics.StrokeCap
import androidx.ui.graphics.StrokeJoin
import androidx.ui.graphics.withSave
import com.ivianuu.essentials.ui.common.withDensity
import com.ivianuu.essentials.util.sourceLocation
import kotlin.math.ceil

/**
 * Sentinel value used to indicate that a dimension is not provided
 */
private const val unset: Float = -1.0f

private val DefaultAlignment = Alignment.Center

/**
 * Draw a vector graphic with the provided width, height and viewport dimensions
 * @param[defaultWidth] Intrinsic width of the Vector in [Dp]
 * @param[defaultHeight] Intrinsic height of hte Vector in [Dp]
 * @param[viewportWidth] Width of the viewport space. The viewport is the virtual canvas where
 * paths are drawn on.
 *  This parameter is optional. Not providing it will use the [defaultWidth] converted to [Px]
 * @param[viewportHeight] Height of hte viewport space. The viewport is the virtual canvas where
 * paths are drawn on.
 *  This parameter is optional. Not providing it will use the [defaultHeight] converted to [Px]
 * @param[tintColor] Optional color used to tint this vector graphic
 * @param[tintBlendMode] Optional blend mode used with [tintColor], default is [BlendMode.srcIn]
 * @param[alignment] Specifies the placement of the vector within the drawing bounds
 * @param[scaleFit] Specifies how the vector is to be scaled within the parent bounds
 */
@Composable
fun DrawVector(
    defaultWidth: Dp,
    defaultHeight: Dp,
    viewportWidth: Float = unset,
    viewportHeight: Float = unset,
    tintColor: Color = DefaultTintColor,
    tintBlendMode: BlendMode = DefaultTintBlendMode,
    alignment: Alignment = DefaultAlignment,
    scaleFit: ScaleFit = ScaleFit.Fit,
    name: String = "",
    children: @Composable() VectorScope.(viewportWidth: Float, viewportHeight: Float) -> Unit
) {
    val widthPx = withDensity { defaultWidth.toPx() }
    val heightPx = withDensity { defaultHeight.toPx() }

    val vpWidth = if (viewportWidth == unset) widthPx.value else viewportWidth
    val vpHeight = if (viewportHeight == unset) heightPx.value else viewportHeight
    DrawVector(
        defaultWidth = widthPx,
        defaultHeight = heightPx,
        viewportWidth = vpWidth,
        viewportHeight = vpHeight,
        tintColor = tintColor,
        tintBlendMode = tintBlendMode,
        alignment = alignment,
        scaleFit = scaleFit,
        name = name,
        children = children
    )
}

/**
 * Draw a vector graphic with the provided width, height and viewport dimensions
 * @param[defaultWidth] Intrinsic width of the Vector in [Px]
 * @param[defaultHeight] Intrinsic height of hte Vector in [Px]
 * @param[viewportWidth] Width of the viewport space. The viewport is the virtual canvas
 *  where paths are drawn on. This parameter is optional. Not providing it will use the
 *  [defaultWidth]
 * @param[viewportHeight] Height of hte viewport space. The viewport is the virtual canvas
 *  where paths are drawn on. This parameter is optional. Not providing it will use the
 *  [defaultHeight]
 * @param[tintColor] Optional color used to tint this vector graphic
 * @param[tintBlendMode] Optional blend mode used with [tintColor], default is [BlendMode.srcIn]
 * @param[alignment] Specifies the placement of the vector within the drawing bounds
 * @param[scaleFit] Specifies how the vector is to be scaled within the parent bounds
 */
@Composable
fun DrawVector(
    defaultWidth: Px,
    defaultHeight: Px,
    viewportWidth: Float = defaultWidth.value,
    viewportHeight: Float = defaultHeight.value,
    tintColor: Color = DefaultTintColor,
    tintBlendMode: BlendMode = DefaultTintBlendMode,
    alignment: Alignment = DefaultAlignment,
    scaleFit: ScaleFit = ScaleFit.Fit,
    name: String = "",
    children: @Composable() VectorScope.(viewportWidth: Float, viewportHeight: Float) -> Unit
) {
    val vector =
        remember(name, viewportWidth, viewportHeight) {
            VectorComponent(
                name,
                viewportWidth,
                viewportHeight,
                defaultWidth,
                defaultHeight
            )
        }

    val ref = compositionReference()
    composeVector(vector, ref, children)
    onPreCommit(vector) {
        onDispose {
            disposeVector(vector, ref)
        }
    }

    val vectorWidth = defaultWidth.value
    val vectorHeight = defaultHeight.value
    val vectorPxSize = PxSize(Px(vectorWidth), Px(vectorHeight))

    Draw { canvas, parentSize ->
        val parentWidth = parentSize.width.value
        val parentHeight = parentSize.height.value
        val scale = scaleFit.scale(vectorPxSize, parentSize)

        val alignedPosition = alignment.align(
            IntPxSize(
                IntPx(ceil(parentWidth - (vectorWidth * scale)).toInt()),
                IntPx(ceil(parentHeight - (vectorHeight * scale)).toInt())
            )
        )

        val translateX = alignedPosition.x.value.toFloat()
        val translateY = alignedPosition.y.value.toFloat()

        // apply the scale to the root of the vector
        vector.root.scaleX = (vectorWidth / viewportWidth) * scale
        vector.root.scaleY = (vectorHeight / viewportHeight) * scale

        canvas.withSave {
            canvas.translate(translateX, translateY)
            vector.draw(canvas, tintColor, tintBlendMode)
        }
    }
}

@Composable
fun VectorScope.Group(
    name: String = DefaultGroupName,
    rotation: Float = DefaultRotation,
    pivotX: Float = DefaultPivotX,
    pivotY: Float = DefaultPivotY,
    scaleX: Float = DefaultScaleX,
    scaleY: Float = DefaultScaleY,
    translationX: Float = DefaultTranslationX,
    translationY: Float = DefaultTranslationY,
    clipPathData: List<PathNode> = EmptyPath,
    children: @Composable() VectorScope.() -> Unit
) {
    composer.emit(
        key = sourceLocation(),
        ctor = { GroupComponent(name = name) },
        update = {
            val node = node as GroupComponent
            node.rotation = rotation
            node.pivotX = pivotX
            node.pivotY = pivotY
            node.scaleX = scaleX
            node.scaleY = scaleY
            node.translationX = translationX
            node.translationY = translationY
            node.clipPathData = clipPathData
        },
        children = {
            children()
        }
    )
}

@Composable
fun VectorScope.Path(
    pathData: List<PathNode>,
    name: String = DefaultPathName,
    fill: Brush? = null,
    fillAlpha: Float = DefaultAlpha,
    stroke: Brush? = null,
    strokeAlpha: Float = DefaultAlpha,
    strokeLineWidth: Float = DefaultStrokeLineWidth,
    strokeLineCap: StrokeCap = DefaultStrokeLineCap,
    strokeLineJoin: StrokeJoin = DefaultStrokeLineJoin,
    strokeLineMiter: Float = DefaultStrokeLineMiter
) {
    composer.emit(
        key = sourceLocation(),
        ctor = { PathComponent(name = name) },
        update = {
            val node = node as PathComponent
            node.pathData = pathData
            node.fill = fill
            node.fillAlpha = fillAlpha
            node.stroke = stroke
            node.strokeAlpha = strokeAlpha
            node.strokeLineWidth = strokeLineWidth
            node.strokeLineCap = strokeLineCap
            node.strokeLineJoin = strokeLineJoin
            node.strokeLineMiter = strokeLineMiter
        }
    )
}