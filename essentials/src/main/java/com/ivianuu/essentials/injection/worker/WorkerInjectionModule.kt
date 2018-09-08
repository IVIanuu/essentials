package com.ivianuu.essentials.injection.worker

import androidx.work.Worker
import dagger.Module
import dagger.android.AndroidInjector
import dagger.multibindings.Multibinds

/**
 * Worker injection module
 */
@Module
abstract class WorkerInjectionModule {

    @Multibinds
    abstract fun workerInjectorFactories(): Map<Class<out Worker>, AndroidInjector.Factory<out Worker>>
}