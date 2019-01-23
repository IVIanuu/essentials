package com.ivianuu.essentials.app

import android.app.Application
import com.ivianuu.injekt.BindingContext
import com.ivianuu.injekt.Definition
import com.ivianuu.injekt.Module
import com.ivianuu.injekt.factory
import com.ivianuu.injekt.module
import com.ivianuu.injekt.multibinding.bindIntoMap
import com.ivianuu.injekt.multibinding.mapBinding

const val APP_INITIALIZERS = "appInitializers"

/**
 * Initializes what ever on app start up
 */
interface AppInitializer {
    fun initialize(app: Application)
}

inline fun <reified T : AppInitializer> Module.appInitializer(
    name: String? = null,
    override: Boolean = false,
    noinline definition: Definition<T>
): BindingContext<T> =
    factory(name, null, override, definition) bindIntoMap (APP_INITIALIZERS to T::class)

inline fun <reified T : AppInitializer> Module.bindAppInitializer(name: String? = null) {
    bindIntoMap<T>(
        APP_INITIALIZERS,
        T::class,
        implementationType = T::class,
        implementationName = name
    )
}

val esAppInitializersModule = module("EsAppInitializersModule") {
    mapBinding(APP_INITIALIZERS)
    bindAppInitializer<RxJavaAppInitializer>()
    bindAppInitializer<StateStoreAppInitializer>()
    bindAppInitializer<TimberAppInitializer>()
}