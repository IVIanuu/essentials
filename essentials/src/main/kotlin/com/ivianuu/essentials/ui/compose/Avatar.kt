package com.ivianuu.essentials.ui.compose

import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.ivianuu.compose.ComponentComposition
import com.ivianuu.compose.View
import com.ivianuu.compose.layoutRes
import com.ivianuu.essentials.R

fun ComponentComposition.Avatar(
    avatar: Drawable? = null,
    avatarRes: Int? = null
) {
    View<ImageView> {
        layoutRes(R.layout.es_avatar)
        bindView {
            when {
                avatar != null -> setImageDrawable(avatar)
                avatarRes != null -> setImageResource(avatarRes)
                else -> setImageDrawable(null)
            }
        }
    }
}