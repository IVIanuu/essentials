package com.ivianuu.essentials.ui.compose

import android.graphics.Bitmap
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.updateLayoutParams
import com.ivianuu.compose.ComponentComposition
import com.ivianuu.compose.ContextAmbient
import com.ivianuu.compose.View
import com.ivianuu.compose.ambient
import com.ivianuu.compose.setBy
import com.ivianuu.essentials.util.getIconColor
import com.ivianuu.kommon.core.content.dp

fun ComponentComposition.Avatar(
    imageLoader: ((ImageView) -> Unit)? = null,
    imageRes: Int? = null,
    imageDrawable: Drawable? = null,
    imageBitmap: Bitmap? = null
) {
    val iconSize = ambient(ContextAmbient).dp(40).toInt()
    Image(
        size = iconSize,
        imageLoader = imageLoader,
        imageRes = imageRes,
        imageDrawable = imageDrawable,
        imageBitmap = imageBitmap
    )
}

fun ComponentComposition.Icon(
    color: Int? = ambient(ContextAmbient).getIconColor(),
    colorMode: PorterDuff.Mode? = PorterDuff.Mode.SRC_IN,
    imageLoader: ((ImageView) -> Unit)? = null,
    imageRes: Int? = null,
    imageDrawable: Drawable? = null,
    imageBitmap: Bitmap? = null
) {
    val iconSize = ambient(ContextAmbient).dp(24).toInt()
    Image(
        size = iconSize,
        color = color,
        colorMode = colorMode,
        imageLoader = imageLoader,
        imageRes = imageRes,
        imageDrawable = imageDrawable,
        imageBitmap = imageBitmap
    )
}

fun ComponentComposition.Image(
    size: Int,
    color: Int? = null,
    colorMode: PorterDuff.Mode? = PorterDuff.Mode.SRC_IN,
    imageLoader: ((ImageView) -> Unit)? = null,
    imageRes: Int? = null,
    imageDrawable: Drawable? = null,
    imageBitmap: Bitmap? = null
) {
    Image(
        width = size,
        height = size,
        color = color,
        colorMode = colorMode,
        imageLoader = imageLoader,
        imageRes = imageRes,
        imageDrawable = imageDrawable,
        imageBitmap = imageBitmap
    )
}

fun ComponentComposition.Image(
    width: Int,
    height: Int,
    color: Int? = null,
    colorMode: PorterDuff.Mode? = PorterDuff.Mode.SRC_IN,
    imageLoader: ((ImageView) -> Unit)? = null,
    imageRes: Int? = null,
    imageDrawable: Drawable? = null,
    imageBitmap: Bitmap? = null
) {
    View<AppCompatImageView> {
        setBy(width, height) {
            updateLayoutParams {
                this.width = width
                this.height = height
            }
        }

        setBy(color, colorMode) {
            if (color != null) {
                if (colorMode != null) {
                    setColorFilter(color, colorMode)
                } else {
                    setColorFilter(color)
                }
            } else {
                clearColorFilter()
            }
        }

        setBy(imageLoader, imageRes, imageDrawable, imageBitmap) {
            when {
                imageLoader != null -> imageLoader(this)
                imageRes != null -> setImageResource(imageRes)
                imageDrawable != null -> setImageDrawable(imageDrawable)
                imageBitmap != null -> setImageBitmap(imageBitmap)
                else -> setImageDrawable(null)
            }
        }
    }
}