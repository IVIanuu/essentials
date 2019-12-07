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
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.ivianuu.injekt.BindingContext
import com.ivianuu.injekt.Factory
import com.ivianuu.injekt.Module
import com.ivianuu.injekt.Name
import com.ivianuu.injekt.Provider
import com.ivianuu.injekt.module
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

@Name
annotation class WorkersMap {
    companion object
}

inline fun <reified T : ListenableWorker> Module.bindWorker(
    name: Any? = null
) {
    withBinding<T>(name) { bindWorker() }
}

inline fun <reified T : ListenableWorker> BindingContext<T>.bindWorker(): BindingContext<T> {
    intoMap<String, ListenableWorker>(
        entryKey = T::class.java.name,
        mapName = WorkersMap
    )
    return this
}
