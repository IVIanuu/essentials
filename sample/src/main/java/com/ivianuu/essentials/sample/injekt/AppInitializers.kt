package com.ivianuu.essentials.sample.injekt

import com.ivianuu.essentials.app.AppInitializer
import com.ivianuu.injekt.Definition
import com.ivianuu.injekt.Module
import com.ivianuu.injekt.factory
import com.ivianuu.injekt.intoClassMap

const val APP_INITIALIZERS = "ai"

inline fun <reified T : AppInitializer> Module.appInitializer(
    name: String? = null,
    override: Boolean = false,
    noinline definition: Definition<T>
) {
    factory(T::class, name, override, definition).intoClassMap(AppInitializer::class)
}