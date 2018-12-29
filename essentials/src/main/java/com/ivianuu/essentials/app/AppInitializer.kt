package com.ivianuu.essentials.app

import android.app.Application
import com.ivianuu.injekt.*
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

fun esAppInitializersModule() = module {
    multiBindingMap<KClass<out AppInitializer>, AppInitializer>(APP_INITIALIZERS)

    appInitializer { RxJavaAppInitializer() }
    appInitializer { StateStoreAppInitializer() }
    appInitializer { TimberAppInitializer() }
}