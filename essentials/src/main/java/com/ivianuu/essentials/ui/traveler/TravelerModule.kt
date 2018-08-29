package com.ivianuu.essentials.ui.traveler

import com.ivianuu.traveler.Router
import com.ivianuu.traveler.Traveler
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Traveler module
 */
@Module
object TravelerModule {

    @JvmStatic
    @Singleton
    @Provides
    fun provideTraveler() = Traveler.create()

    @JvmStatic
    @Provides
    fun provideNavigatorHolder(traveler: Traveler<Router>) = traveler.navigatorHolder

    @JvmStatic
    @Provides
    fun provideRouter(traveler: Traveler<Router>) = traveler.router

}