package com.ivianuu.essentials.apps.ui

import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.ivianuu.compose.ComponentComposition
import com.ivianuu.essentials.ui.compose.Avatar

fun ComponentComposition.AppIcon(packageName: String) {
    Avatar(imageLoader = {
        Glide.with(it)
            .load(com.ivianuu.essentials.apps.glide.AppIcon(packageName))
            .apply(
                RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
            )
            .into(it)
    })
}