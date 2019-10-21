/*
 * Copyright 2019 Manuel Wrage
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

/**
 * Uses injekt to instantiate workers
 */
@Inject
class InjektWorkerFactory(
    @WorkersMap private val workers: Map<String, Provider<ListenableWorker>>
) : WorkerFactory() {

    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        return workers[workerClassName]?.invoke { parametersOf(appContext, workerParameters) }
            ?: error("Could not find a worker for $workerClassName")
    }

}

/**
 * Contains the [InjektWorkerFactory]
 */
val workerInjectionModule = module {
    map<String, ListenableWorker>(WorkersMap)
    withBinding<InjektWorkerFactory> { bindType<WorkerFactory>() }
}

/**
 * Defines a [Worker]
 */
typealias WorkerDefinition<T> = DefinitionContext.(context: Context, workerParams: WorkerParameters) -> T

@Name(WorkersMap.Companion::class)
annotation class WorkersMap {
    companion object
}

/**
 * Defines a [Worker] which will be used in conjunction with the [InjektWorkerFactory]
 */
inline fun <reified T : ListenableWorker> Module.worker(
    name: Any? = null,
    noinline definition: WorkerDefinition<T>
): BindingContext<T> = factory(name) { (context: Context, workerParams: WorkerParameters) ->
    definition(context, workerParams)
}.bindWorker()

inline fun <reified T : ListenableWorker> Module.bindWorker(
    name: Any? = null
) {
    withBinding<T>(name) { bindWorker() }
}

inline fun <reified T : ListenableWorker> BindingContext<T>.bindWorker(): BindingContext<T> {
    intoMap(
        mapKeyType = typeOf(),
        mapValueType = typeOf<ListenableWorker>(),
        entryKey = T::class.java.name,
        mapName = WorkersMap
    )
    return this
}