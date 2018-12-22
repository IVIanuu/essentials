package com.ivianuu.essentials.ui.traveler

import com.ivianuu.traveler.Router
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
    fun provideRouter() = Router()

}