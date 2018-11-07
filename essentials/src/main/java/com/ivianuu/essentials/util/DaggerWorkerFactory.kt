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

package com.ivianuu.essentials.util

import android.content.Context
import androidx.work.NonBlockingWorker
import androidx.work.Worker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import javax.inject.Inject
import javax.inject.Provider

/**
 * Dagger worker factory
 */
class DaggerWorkerFactory @Inject constructor(
    private val creators: Map<Class<out Worker>, @JvmSuppressWildcards Provider<Worker>>
) : WorkerFactory {

    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): Worker? {
        val clazz = Class.forName(workerClassName)

        val creator = creators[clazz] ?: creators.entries.firstOrNull {
            clazz.isAssignableFrom(it.key)
        }?.value ?: throw IllegalArgumentException("unknown worker class $clazz")
        try {
            @Suppress("UNCHECKED_CAST")
            return creator.get().apply { initialize(appContext, workerParameters) }
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    private fun Worker.initialize(appContext: Context, workerParameters: WorkerParameters) {
        val internalInitMethod = NonBlockingWorker::class.java.getDeclaredMethod(
            "internalInit",
            Context::class.java,
            WorkerParameters::class.java
        )
        internalInitMethod.isAccessible = true
        internalInitMethod.invoke(
            this,
            appContext,
            workerParameters
        )
    }
}