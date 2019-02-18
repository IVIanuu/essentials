/*
 * Copyright 2018 Manuel Wrage
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ivianuu.essentials.work

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.Worker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.ivianuu.injekt.BindingContext
import com.ivianuu.injekt.DefinitionContext
import com.ivianuu.injekt.Module
import com.ivianuu.injekt.Provider
import com.ivianuu.injekt.bindType
import com.ivianuu.injekt.factory
import com.ivianuu.injekt.module
import com.ivianuu.injekt.multibinding.bindIntoMap
import com.ivianuu.injekt.multibinding.getProviderMap
import com.ivianuu.injekt.multibinding.mapBinding
import com.ivianuu.injekt.parametersOf

/**
 * Uses [Injekt] to instantiate workers
 */
class InjektWorkerFactory(
    private val workers: Map<String, Provider<Worker>>
) : WorkerFactory() {

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
    mapBinding(WORKER_MAP)
    factory { InjektWorkerFactory(getProviderMap(WORKER_MAP)) } bindType WorkerFactory::class
}

/**
 * Defines a [Worker]
 */
typealias WorkerDefinition<T> = DefinitionContext.(context: Context, workerParams: WorkerParameters) -> T

/**
 * Defines a [Worker] which will be used in conjunction with the [InjektWorkerFactory]
 */
inline fun <reified T : Worker> Module.worker(
    name: String? = null,
    override: Boolean = false,
    noinline definition: WorkerDefinition<T>
): BindingContext<T> {
    return factory(name, null, override) { (context: Context, workerParams: WorkerParameters) ->
        definition(this, context, workerParams)
    } bindIntoMap (WORKER_MAP to T::class.java.name)
}

/**
 * Binds a already existing [Worker]
 */
inline fun <reified T : Worker> Module.bindWorker(name: String? = null) {
    bindIntoMap<T>(
        mapName = WORKER_MAP,
        mapKey = T::class.java.name,
        implementationType = T::class,
        implementationName = name
    )
}