package com.ivianuu.essentials.android.ui.transform

import androidx.compose.Immutable
import androidx.ui.core.DrawModifier
import androidx.ui.graphics.Canvas
import androidx.ui.graphics.withSave
import androidx.ui.unit.Density
import androidx.ui.unit.Dp
import androidx.ui.unit.PxSize
import androidx.ui.unit.dp

interface TransformModifier : DrawModifier {
    override fun draw(density: Density, drawContent: () -> Unit, canvas: Canvas, size: PxSize) {
        canvas.withSave {
            transform(density, canvas, size)
            drawContent()
        }
    }

    fun transform(density: Density, canvas: Canvas, size: PxSize)
}

@Immutable
data class TransformScale(
    val scaleX: Float = 1f,
    val scaleY: Float = 1f,
    val pivotX: Float = 0f,
    val pivotY: Float = 0f
) : TransformModifier {
    override fun transform(density: Density, canvas: Canvas, size: PxSize) {
        canvas.translate((size.width * pivotX).value, (size.height * pivotY).value)
        canvas.scale(scaleX, scaleY)
        canvas.translate(-(size.width * pivotX).value, -(size.height * pivotY).value)
    }
}

@Immutable
data class TransformSkew(
    val skewX: Float = 0f,
    val skewY: Float = 0f
) : TransformModifier {
    override fun transform(density: Density, canvas: Canvas, size: PxSize) {
        canvas.skew(skewX, skewY)
    }

    @Immutable
    data class Radians(
        val skewXRadians: Float,
        val skewYRadions: Float
    ) : TransformModifier {
        override fun transform(density: Density, canvas: Canvas, size: PxSize) {
            canvas.skewRad(skewXRadians, skewYRadions)
        }
    }
}

object TransformRotation {
    @Immutable
    data class Degrees(val degrees: Float) : TransformModifier {
        override fun transform(density: Density, canvas: Canvas, size: PxSize) {
            canvas.rotate(degrees)

        }
    }

    @Immutable
    data class Radians(val radians: Float) : TransformModifier {
        override fun transform(density: Density, canvas: Canvas, size: PxSize) {
            canvas.rotateRad(radians)
        }
    }
}

@Immutable
data class TransformTranslation(
    val translationX: Dp = 0.dp,
    val translationY: Dp = 0.dp
) : TransformModifier {
    override fun transform(density: Density, canvas: Canvas, size: PxSize) {
        with(density) {
            canvas.translate(
                translationX.toPx().value,
                translationY.toPx().value
            )
        }
    }

    @Immutable
    data class Fraction(
        val translationXFraction: Float = 0f,
        val translationYFraction: Float = 0f
    ) : TransformModifier {
        override fun transform(density: Density, canvas: Canvas, size: PxSize) {
            canvas.translate(
                (size.width * translationXFraction).value,
                (size.height * translationYFraction).value
            )
        }
    }
}
