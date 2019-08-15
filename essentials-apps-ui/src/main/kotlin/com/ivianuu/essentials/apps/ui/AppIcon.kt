package com.ivianuu.essentials.apps.ui

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.ivianuu.compose.ComponentComposition
import com.ivianuu.compose.ViewByLayoutRes
import com.ivianuu.compose.set

fun ComponentComposition.AppIcon(packageName: String) {
    ViewByLayoutRes<ImageView>(layoutRes = R.layout.es_avatar) {
        set(packageName) {
            Glide.with(this)
                .load(com.ivianuu.essentials.apps.glide.AppIcon(it))
                .apply(
                    RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                )
                .into(this)
        }
    }
}