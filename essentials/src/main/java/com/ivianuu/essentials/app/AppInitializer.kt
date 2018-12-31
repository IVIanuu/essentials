package com.ivianuu.essentials.app

import android.app.Application
import com.ivianuu.essentials.injection.multibinding.bindIntoClassMap
import com.ivianuu.essentials.injection.multibinding.classMapBinding
import com.ivianuu.essentials.injection.multibinding.intoClassMap
import com.ivianuu.injekt.Definition
import com.ivianuu.injekt.Module
import com.ivianuu.injekt.factory
import com.ivianuu.injekt.module

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
) = factory(name, override, definition) intoClassMap APP_INITIALIZERS

inline fun <reified T : AppInitializer> Module.bindAppInitializer() =
    bindIntoClassMap<AppInitializer, T>(APP_INITIALIZERS)

val esAppInitializersModule = module {
    classMapBinding<AppInitializer>(APP_INITIALIZERS)
    bindAppInitializer<RxJavaAppInitializer>()
    bindAppInitializer<StateStoreAppInitializer>()
    bindAppInitializer<TimberAppInitializer>()
}