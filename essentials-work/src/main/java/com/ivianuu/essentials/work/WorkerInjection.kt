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
import androidx.work.*
import com.ivianuu.essentials.app.AppInitializer
import com.ivianuu.essentials.app.appInitializer
import com.ivianuu.injekt.*
import com.ivianuu.injekt.multibinding.bindIntoMap
import com.ivianuu.injekt.multibinding.getProviderMap
import com.ivianuu.injekt.multibinding.mapBinding

/**
 * Uses injekt to instantiate workers
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
object WorkerMap : StringQualifier("WorkerMap")

/**
 * Contains the [InjektWorkerFactory]
 */
val workerInjectionModule = module {
    mapBinding(WorkerMap)
    factory { InjektWorkerFactory(getProviderMap(WorkerMap)) } bindType WorkerFactory::class
}

/**
 * Defines a [Worker]
 */
typealias WorkerDefinition<T> = DefinitionContext.(context: Context, workerParams: WorkerParameters) -> T

/**
 * Defines a [Worker] which will be used in conjunction with the [InjektWorkerFactory]
 */
inline fun <reified T : Worker> Module.worker(
    qualifier: Qualifier? = null,
    override: Boolean = false,
    noinline definition: WorkerDefinition<T>
): BindingContext<T> {
    return factory(qualifier, null, override) { (context: Context, workerParams: WorkerParameters) ->
        definition(this, context, workerParams)
    } bindIntoMap (WorkerMap to T::class.java.name)
}

/**
 * Binds a already existing [Worker]
 */
inline fun <reified T : Worker> Module.bindWorker(qualifier: Qualifier? = null) {
    bindIntoMap<T>(
        mapQualifier = WorkerMap,
        mapKey = T::class.java.name,
        implementationQualifier = qualifier
    )
}

/**
 * Module for the [WorkerAppInitializer]
 */
val workerInitializerModule = module {
    appInitializer { WorkerAppInitializer(get(), get()) }
}

/**
 * Initializes the [WorkManager] with a injected [WorkerFactory]
 */
class WorkerAppInitializer(
    private val context: Context,
    private val workerFactory: WorkerFactory
) : AppInitializer {
    override fun initialize() {
        WorkManager.initialize(
            context, Configuration.Builder()
                .setWorkerFactory(workerFactory)
                .build()
        )
    }
}