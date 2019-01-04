package com.ivianuu.essentials.work

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.Worker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.ivianuu.essentials.injection.multibinding.StringMultiBindingMap
import com.ivianuu.essentials.injection.multibinding.intoStringMap
import com.ivianuu.essentials.injection.multibinding.toProviderMap
import com.ivianuu.injekt.*

/**
 * Uses [Injekt] to instantiate workers
 */
class InjektWorkerFactory(
    workersMap: StringMultiBindingMap<Worker>
) : WorkerFactory() {

    private val workers = workersMap.toProviderMap()

    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        return workers[workerClassName]?.get { parametersOf(appContext, workerParameters) }
            ?: error("Could not find a worker for $workerClassName")
    }

}

/**
 * The map of [Worker]s used by the [InjektWorkerFactory]
 */
const val WORKER_MAP = "workers"

/**
 * Contains the [InjektWorkerFactory]
 */
val workerInjectionModule = module("WorkerInjectionModule") {
    factory { InjektWorkerFactory(get(WORKER_MAP)) }
}

/**
 * Defines a [Worker]
 */
typealias WorkerDefinition<T> = DefinitionContext.(context: Context, workerParams: WorkerParameters) -> T

/**
 * Defines a [Worker] which will be used in conjunction with the [InjektWorkerFactory]
 */
inline fun <reified T : Worker> ModuleContext.worker(
    name: String? = null,
    override: Boolean = false,
    noinline definition: WorkerDefinition<T>
) = factory(name, override) { (context: Context, workerParams: WorkerParameters) ->
    definition(this, context, workerParams)
} intoStringMap (WORKER_MAP to T::class.java.name)