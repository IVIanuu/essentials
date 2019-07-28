package com.ivianuu.essentials.ui.compose.view

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import androidx.compose.ViewComposition
import com.ivianuu.essentials.ui.compose.sourceLocation

inline fun ViewComposition.ImageView(noinline block: ViewDsl<AppCompatImageView>.() -> Unit) =
    ImageView(sourceLocation(), block)

fun ViewComposition.ImageView(key: Any, block: ViewDsl<AppCompatImageView>.() -> Unit) =
    View(key, { AppCompatImageView(it) }, block)

data class Image(
    val drawable: Drawable? = null,
    val bitmap: Bitmap? = null,
    val res: Int? = null
) {}

fun <T : ImageView> ViewDsl<T>.image(image: Image) {
    set(image) {
        when {
            image.drawable != null -> setImageDrawable(drawable)
            image.bitmap != null -> setImageBitmap(image.bitmap)
            image.res != null -> setImageResource(image.res)
            else -> setImageDrawable(null)
        }
    }
}

fun <T : ImageView> ViewDsl<T>.image(
    drawable: Drawable? = null,
    bitmap: Bitmap? = null,
    res: Int? = null
) {
    image(Image(drawable, bitmap, res))
}