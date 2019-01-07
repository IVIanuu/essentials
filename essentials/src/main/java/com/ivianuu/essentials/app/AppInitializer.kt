package com.ivianuu.essentials.app

import android.app.Application
import com.ivianuu.injekt.BeanDefinition
import com.ivianuu.injekt.Definition
import com.ivianuu.injekt.Module
import com.ivianuu.injekt.ModuleContext
import com.ivianuu.injekt.factory
import com.ivianuu.injekt.module
import com.ivianuu.injekt.multibinding.bindIntoClassMap
import com.ivianuu.injekt.multibinding.classMapBinding
import com.ivianuu.injekt.multibinding.intoClassMap

const val APP_INITIALIZERS = "appInitializers"

/**
 * Initializes what ever on app start up
 */
interface AppInitializer {
    fun initialize(app: Application)
}

inline fun <reified T : AppInitializer> ModuleContext.appInitializer(
    name: String? = null,
    override: Boolean = false,
    noinline definition: Definition<T>
): BeanDefinition<T> = factory(name, override, definition) intoClassMap APP_INITIALIZERS

inline fun <reified T : AppInitializer> ModuleContext.bindAppInitializer(): BeanDefinition<AppInitializer> =
    bindIntoClassMap<AppInitializer, T>(APP_INITIALIZERS)

val esAppInitializersModule = module("EsAppInitializersModule") {
    classMapBinding<AppInitializer>(APP_INITIALIZERS)

    appInitializer { RxJavaAppInitializer() }
    appInitializer { StateStoreAppInitializer() }
    appInitializer { TimberAppInitializer() }
}