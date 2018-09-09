package com.ivianuu.essentials.injection.view

import android.view.View
import dagger.Module
import dagger.android.AndroidInjector
import dagger.multibindings.Multibinds

/**
 * View injection module
 */
@Module
abstract class ViewInjectionModule {

    @Multibinds
    abstract fun viewInjectorFactories(): Map<Class<out View>, AndroidInjector.Factory<out View>>
}