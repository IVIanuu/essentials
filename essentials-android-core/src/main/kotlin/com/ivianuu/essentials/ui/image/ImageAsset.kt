package com.ivianuu.essentials.ui.image

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.compose.Composable
import androidx.core.graphics.drawable.toBitmap
import androidx.ui.core.ContentScale
import androidx.ui.geometry.Size
import androidx.ui.graphics.AndroidImageAssetAccessor
import androidx.ui.graphics.BlendMode
import androidx.ui.graphics.Canvas
import androidx.ui.graphics.Color
import androidx.ui.graphics.ColorFilter
import androidx.ui.graphics.ImageAsset
import androidx.ui.graphics.vector.Group
import androidx.ui.graphics.vector.Path
import androidx.ui.graphics.vector.VectorAsset
import androidx.ui.graphics.vector.VectorComponent
import androidx.ui.graphics.vector.VectorGroup
import androidx.ui.graphics.vector.VectorPath
import androidx.ui.graphics.vector.VectorScope
import androidx.ui.graphics.vector.composeVector
import androidx.ui.unit.Density

fun Drawable.toImageAsset(): ImageAsset = toBitmap().toImageAsset()

fun Bitmap.toImageAsset(): ImageAsset = AndroidImageAssetAccessor.createAndroidImage(this)

fun ImageAsset.toBitmap(): Bitmap = AndroidImageAssetAccessor.getBitmap(this)

fun VectorAsset.toBitmap(
    context: Context,
    colorFilter: ColorFilter = ColorFilter(Color.White, BlendMode.srcIn)
): Bitmap {
    val density = Density(context)
    val vector = with(density) {
        VectorComponent(
            defaultWidth.toPx(),
            defaultHeight.toPx(),
            defaultWidth.toPx(),
            defaultHeight.toPx()
        )
    }

    val composition = composeVector(vector) { _, _ ->
        RenderVectorGroup(group = root)
    }

    val vectorWidth = vector.defaultWidth
    val vectorHeight = vector.defaultHeight

    val bitmap =
        Bitmap.createBitmap(vectorWidth.toInt(), vectorHeight.toInt(), Bitmap.Config.ARGB_8888)

    val scale = ContentScale.Fit.scale(
        srcSize = Size(vector.defaultWidth, vector.defaultHeight),
        dstSize = Size(bitmap.width.toFloat(), bitmap.height.toFloat())
    )

    vector.root.scaleX = (vectorWidth / viewportWidth) * scale
    vector.root.scaleY = (vectorWidth / viewportWidth) * scale
    val canvas = Canvas(android.graphics.Canvas(bitmap))
    vector.draw(canvas, 1f, colorFilter)

    composition.dispose()

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
