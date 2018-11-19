package com.ivianuu.essentials.injection

import com.ivianuu.essentials.app.AppInitializer
import dagger.MapKey
import dagger.Module
import dagger.multibindings.Multibinds
import kotlin.reflect.KClass

/**
 * Map key for [AppInitializer]s
 */
@MapKey
annotation class AppInitializerKey(val value: KClass<out AppInitializer>)

/**
 * App initializer module
 */
@Module
internal abstract class AppInitializerModule {

    @Multibinds
    abstract fun bindAppInitializers(): Map<Class<out AppInitializer>, @JvmSuppressWildcards AppInitializer>

}