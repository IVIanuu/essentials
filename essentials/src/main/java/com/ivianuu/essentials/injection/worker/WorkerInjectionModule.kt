package com.ivianuu.essentials.injection.worker

import androidx.work.Worker
import androidx.work.WorkerFactory
import com.ivianuu.essentials.util.worker.DaggerWorkerFactory
import dagger.Binds
import dagger.Module
import dagger.multibindings.Multibinds

/**
 * Worker injection module
 */
@Module
abstract class WorkerInjectionModule {

    @Multibinds
    abstract fun workerProviders(): Map<Class<out Worker>, Worker>

    @Binds
    abstract fun bindWorkerFactory(factory: DaggerWorkerFactory): WorkerFactory

}