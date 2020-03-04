package com.ivianuu.essentials.ui.image

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.compose.Composable
import androidx.core.graphics.drawable.toBitmap
import androidx.ui.graphics.AndroidImageAssetAccessor
import androidx.ui.graphics.Canvas
import androidx.ui.graphics.Color
import androidx.ui.graphics.ImageAsset
import androidx.ui.graphics.ScaleFit
import androidx.ui.graphics.vector.Group
import androidx.ui.graphics.vector.Path
import androidx.ui.graphics.vector.VectorAsset
import androidx.ui.graphics.vector.VectorComponent
import androidx.ui.graphics.vector.VectorGroup
import androidx.ui.graphics.vector.VectorPath
import androidx.ui.graphics.vector.VectorScope
import androidx.ui.graphics.vector.composeVector
import androidx.ui.graphics.vector.disposeVector
import androidx.ui.unit.Density
import androidx.ui.unit.PxSize
import androidx.ui.unit.Size
import androidx.ui.unit.dp
import androidx.ui.unit.px
import androidx.ui.unit.round

val AvatarSize = Size(40.dp, 40.dp)

fun Drawable.toImageAsset(): ImageAsset = toBitmap().toImageAsset()

fun Bitmap.toImageAsset(): ImageAsset = AndroidImageAssetAccessor.createAndroidImage(this)

fun ImageAsset.toBitmap(): Bitmap = nativeImage

fun VectorAsset.toBitmap(
    context: Context,
    color: Color = Color.White
): Bitmap {
    val density = Density(context)
    val vector = with(density) {
        VectorComponent(
            name,
            defaultWidth.toPx().value,
            defaultHeight.toPx().value,
            defaultWidth.toPx(),
            defaultHeight.toPx()
        )
    }

    composeVector(vector) { _, _ ->
        RenderVectorGroup(group = root)
    }

    val vectorWidth = vector.defaultWidth.round().value
    val vectorHeight = vector.defaultHeight.round().value

    val bitmap = Bitmap.createBitmap(vectorWidth, vectorHeight, Bitmap.Config.ARGB_8888)

    val scale = ScaleFit.Fit.scale(
        srcSize = PxSize(vector.defaultWidth, vector.defaultHeight),
        dstSize = PxSize(bitmap.width.px, bitmap.height.px)
    )

    vector.root.scaleX = (vectorWidth / viewportWidth) * scale
    vector.root.scaleY = (vectorWidth / viewportWidth) * scale
    val canvas = Canvas(android.graphics.Canvas(bitmap))
    vector.draw(canvas, tintColor = color)

    disposeVector(vector)

    return bitmap
}

@Composable
private fun VectorScope.RenderVectorGroup(group: VectorGroup) {
    for (vectorNode in group) {
        if (vectorNode is VectorPath) {
            Path(
                pathData = vectorNode.pathData,
                name = vectorNode.name,
                fill = vectorNode.fill,
                fillAlpha = vectorNode.fillAlpha,
                stroke = vectorNode.stroke,
                strokeAlpha = vectorNode.strokeAlpha,
                strokeLineWidth = vectorNode.strokeLineWidth,
                strokeLineCap = vectorNode.strokeLineCap,
                strokeLineJoin = vectorNode.strokeLineJoin,
                strokeLineMiter = vectorNode.strokeLineMiter
            )
        } else if (vectorNode is VectorGroup) {
            Group(
                name = vectorNode.name,
                rotation = vectorNode.rotation,
                scaleX = vectorNode.scaleX,
                scaleY = vectorNode.scaleY,
                translationX = vectorNode.translationX,
                translationY = vectorNode.translationY,
                pivotX = vectorNode.pivotX,
                pivotY = vectorNode.pivotY,
                clipPathData = vectorNode.clipPathData
            ) {
                RenderVectorGroup(group = vectorNode)
            }
        }
    }
}