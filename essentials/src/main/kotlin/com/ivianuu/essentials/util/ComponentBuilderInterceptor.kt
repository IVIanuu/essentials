package com.ivianuu.essentials.util

import com.ivianuu.injekt.ComponentBuilder

interface ComponentBuilderInterceptor {
    fun ComponentBuilder.buildComponent() {
    }
}
