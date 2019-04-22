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
import com.ivianuu.injekt.*
import com.ivianuu.injekt.multibinding.MapName
import com.ivianuu.injekt.multibinding.bindIntoMap
import com.ivianuu.injekt.multibinding.getProviderMap
import com.ivianuu.injekt.provider.Provider
import kotlin.reflect.KClass

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
val workerMap = MapName<String, Worker>()

/**
 * Contains the [InjektWorkerFactory]
 */
val workerInjectionModule = module {
    factoryBuilder<InjektWorkerFactory> {
        definition { InjektWorkerFactory(getProviderMap(workerMap)) }
        bindType<WorkerFactory>()
    }
}

/**
 * Defines a [Worker]
 */
typealias WorkerDefinition<T> = DefinitionContext.(context: Context, workerParams: WorkerParameters) -> T

/**
 * Defines a [Worker] which will be used in conjunction with the [InjektWorkerFactory]
 */
inline fun <reified T : Worker> ModuleBuilder.worker(
    name: Any? = null,
    override: Boolean = false,
    noinline definition: WorkerDefinition<T>
) {
    worker(T::class, name, override, definition)
}

/**
 * Defines a [Worker] which will be used in conjunction with the [InjektWorkerFactory]
 */
fun <T : Worker> ModuleBuilder.worker(
    type: KClass<*>,
    name: Any? = null,
    override: Boolean = false,
    definition: WorkerDefinition<T>
) {
    workerBuilder(type, name, override, definition) {}
}

/**
 * Defines a [Worker] which will be used in conjunction with the [InjektWorkerFactory]
 */
inline fun <reified T : Worker> ModuleBuilder.workerBuilder(
    name: Any? = null,
    override: Boolean = false,
    noinline definition: WorkerDefinition<T>,
    noinline block: BindingBuilder<T>.() -> Unit
) {
    workerBuilder(T::class, name, override, definition, block)
}

/**
 * Defines a [Worker] which will be used in conjunction with the [InjektWorkerFactory]
 */
fun <T : Worker> ModuleBuilder.workerBuilder(
    type: KClass<*>,
    name: Any? = null,
    override: Boolean = false,
    definition: WorkerDefinition<T>,
    block: BindingBuilder<T>.() -> Unit
) {
    factoryBuilder<T>(type, name, override) {
        definition { (context: Context, workerParams: WorkerParameters) ->
            definition(context, workerParams)
        }
        bindIntoMap(workerMap, type.java.name)
        block()
    }
}