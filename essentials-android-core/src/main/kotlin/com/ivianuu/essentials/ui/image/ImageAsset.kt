package com.ivianuu.essentials.ui.image

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.widget.FrameLayout
import androidx.compose.ui.graphics.AndroidImageAssetAccessor
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageAsset
import androidx.compose.ui.graphics.vector.VectorAsset
import androidx.compose.ui.unit.Density
import androidx.core.graphics.drawable.toBitmap

fun Drawable.toImageAsset(): ImageAsset = toBitmap().toImageAsset()

fun Bitmap.toImageAsset(): ImageAsset = AndroidImageAssetAccessor.createAndroidImage(this)

fun ImageAsset.toBitmap(): Bitmap = AndroidImageAssetAccessor.getBitmap(this)

fun VectorAsset.toBitmap(
    context: Context,
    colorFilter: ColorFilter = ColorFilter(Color.White, BlendMode.SrcIn)
): Bitmap {
    val view = FrameLayout(context)

    val density = Density(context)

    val bitmap = with(density) {
        Bitmap.createBitmap(
            defaultWidth.toIntPx(),
            defaultHeight.toIntPx(),
            Bitmap.Config.ARGB_8888
        )
    }

    // todo
    /*val composition = view.setContent(Recomposer()) {
        Image(
            asset = this,
            colorFilter = colorFilter
        )
    }

    val specWidth = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
    val specHeight = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
    view.measure(specWidth, specHeight)
    view.layout(0, 0, view.measuredWidth, view.measuredHeight)
    view.draw(android.graphics.Canvas(bitmap))

    composition.dispose()*/

    return bitmap
}
