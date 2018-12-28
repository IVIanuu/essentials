package com.ivianuu.essentials.app.glide

import android.graphics.drawable.Drawable
import com.bumptech.glide.Glide
import com.ivianuu.injekt.Component
import com.ivianuu.injekt.get

fun initializeAppGlide(component: Component) {
    Glide.get(component.get()).registry
        .append(
            AppIcon::class.java,
            Drawable::class.java,
            component.get<AppIconModelLoader.Factory>()
        )
}