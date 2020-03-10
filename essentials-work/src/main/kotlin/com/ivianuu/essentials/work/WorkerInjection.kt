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
import androidx.work.Configuration
import androidx.work.ListenableWorker
import androidx.work.WorkManager
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.ivianuu.essentials.app.AppInitializer
import com.ivianuu.essentials.app.bindAppInitializerIntoMap
import com.ivianuu.injekt.ComponentBuilder
import com.ivianuu.injekt.Factory
import com.ivianuu.injekt.Module
import com.ivianuu.injekt.Provider
import com.ivianuu.injekt.Qualifier
import com.ivianuu.injekt.QualifierMarker
import com.ivianuu.injekt.alias
import com.ivianuu.injekt.common.map
import com.ivianuu.injekt.factory
import com.ivianuu.injekt.keyOf
import com.ivianuu.injekt.parametersOf

/**
 * Uses injekt to instantiate workers
 */
@Factory
class InjektWorkerFactory(
    @WorkersMap private val workers: Map<String, Provider<ListenableWorker>>
) : WorkerFactory() {

    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        return workers[workerClassName]?.invoke(
            parameters = parametersOf(
                appContext,
                workerParameters
            )
        )
            ?: error("Could not find a worker for $workerClassName")
    }
}

/**
 * Contains the [InjektWorkerFactory]
 */
/**
 * Contains the [InjektWorkerFactory]
 */
val WorkInjectionModule = Module {
    map<String, ListenableWorker>(mapQualifier = WorkersMap)
    alias<InjektWorkerFactory, WorkerFactory>()
    modules(EsWorkModule)
}

private val EsWorkModule = Module {
    factory { WorkManager.getInstance(get()) }
    bindAppInitializerIntoMap<WorkerAppInitializer>()
}

/**
 * Initializes the [WorkManager] with a injected [WorkerFactory]
 */
@Factory
internal class WorkerAppInitializer(
    context: Context,
    workerFactory: WorkerFactory
) : AppInitializer {
    init {
        WorkManager.initialize(
            context, Configuration.Builder()
                .setWorkerFactory(workerFactory)
                .build()
        )
    }
}

@QualifierMarker
annotation class WorkersMap {
    companion object : Qualifier.Element
}

inline fun <reified T : ListenableWorker> ComponentBuilder.bindWorkerIntoMap(
    workerQualifier: Qualifier = Qualifier.None
) {
    map<String, ListenableWorker>(mapQualifier = WorkersMap) {
        put(T::class.java.name, keyOf<T>(qualifier = workerQualifier))
    }
}
