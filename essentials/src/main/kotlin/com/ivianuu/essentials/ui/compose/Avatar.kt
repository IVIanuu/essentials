package com.ivianuu.essentials.ui.compose

import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.ivianuu.compose.ComponentComposition
import com.ivianuu.compose.ViewByLayoutRes
import com.ivianuu.compose.setBy
import com.ivianuu.essentials.R

fun ComponentComposition.Avatar(
    avatar: Drawable? = null,
    avatarRes: Int? = null
) {
    ViewByLayoutRes<ImageView>(layoutRes = R.layout.es_avatar) {
        setBy(avatar, avatarRes) {
            when {
                avatar != null -> setImageDrawable(avatar)
                avatarRes != null -> setImageResource(avatarRes)
                else -> setImageDrawable(null)
            }
        }
    }
}