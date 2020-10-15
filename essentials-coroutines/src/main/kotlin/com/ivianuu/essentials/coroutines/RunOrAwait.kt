/*
 * Copyright 2020 Manuel Wrage
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

package com.ivianuu.essentials.coroutines

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean

suspend fun <T> runOrAwait(
    key: Any,
    block: suspend CoroutineScope.() -> T
): T = coroutineScope {
    val deferred = deferreds[key]?.takeIf { it.isActive }
        ?: async(block = block).also { deferreds[key] = it }

    if (deferreds.size > 100 && !deferredsCleanLaunched.getAndSet(true)) {
        launch {
            // Remove any complete entries
            deferreds.entries.removeAll { it.value.isCompleted }
            deferredsCleanLaunched.set(false)
        }
    }

    @Suppress("UNCHECKED_CAST")
    deferred.await() as T
}

private val deferreds = ConcurrentHashMap<Any, Deferred<*>>()
private val deferredsCleanLaunched = AtomicBoolean()
