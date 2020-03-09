package com.ivianuu.essentials.app

import com.ivianuu.injekt.ComponentBuilder

interface ComponentBuilderInterceptor {
    fun ComponentBuilder.buildComponent() {
    }
}
