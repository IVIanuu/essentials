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

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.withContext

fun <T> Flow<T>.onCancel(
    action: suspend FlowCollector<T>.() -> Unit
) = object : Flow<T> {
    override suspend fun collect(collector: FlowCollector<T>) {
        try {
            this@onCancel.collect { value ->
                try {
                    collector.emit(value)
                } catch (e: CancellationException) {
                    throw CollectorCancellationException(e)
                }
            }
        } catch (e: CancellationException) {
            if (e !is CollectorCancellationException) {
                withContext(NonCancellable) {
                    action(collector)
                }
            }
            throw e
        }
    }
}

private class CollectorCancellationException(
    override val cause: CancellationException
) : CancellationException(null)
