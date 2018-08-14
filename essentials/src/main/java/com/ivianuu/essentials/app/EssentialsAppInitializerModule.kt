package com.ivianuu.essentials.app

import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoSet

/**
 * Essentials app initializer module
 */
@Module
abstract class EssentialsAppInitializerModule {

    @IntoSet
    @Binds
    abstract fun bindFabricAppInitializer(fabricAppInitializer: FabricAppInitializer): AppInitializer

    @IntoSet
    @Binds
    abstract fun bindGlideAppInitializer(glideAppInitializer: GlideAppInitializer): AppInitializer

    @IntoSet
    @Binds
    abstract fun bindRxJavaAppInitializer(rxJavaAppInitializer: RxJavaAppInitializer): AppInitializer

    @IntoSet
    @Binds
    abstract fun bindTimberAppInitializer(timberAppInitializer: TimberAppInitializer): AppInitializer

}