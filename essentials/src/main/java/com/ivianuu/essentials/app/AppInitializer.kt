package com.ivianuu.essentials.app

import android.app.Application
import com.ivianuu.essentials.injection.bindIntoMap
import com.ivianuu.essentials.injection.intoMap
import com.ivianuu.essentials.injection.mapBinding
import com.ivianuu.injekt.Definition
import com.ivianuu.injekt.Module
import com.ivianuu.injekt.factory
import com.ivianuu.injekt.module
import kotlin.reflect.KClass

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
) = factory(name, override, definition) intoMap (APP_INITIALIZERS to T::class)

inline fun <reified T : AppInitializer> Module.bindAppInitializer() =
    bindIntoMap<KClass<out AppInitializer>, AppInitializer, T>(APP_INITIALIZERS, T::class)

val esAppInitializersModule = module("EsAppInitializersModule") {
    mapBinding<KClass<out AppInitializer>, AppInitializer>(APP_INITIALIZERS)

    bindAppInitializer<RxJavaAppInitializer>()
    bindAppInitializer<StateStoreAppInitializer>()
    bindAppInitializer<TimberAppInitializer>()
}