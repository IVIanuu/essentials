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

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow

fun <T> Flow<T>.launchOnStart(block: suspend CoroutineScope.() -> Unit) = flow {
    coroutineScope {
        launch(block = block)
        emitAll(this@launchOnStart)
    }
}

fun <T> Flow<T>.launchOnEach(block: suspend CoroutineScope.(T) -> Unit) = flow {
    coroutineScope {
        this@launchOnEach.collect { value ->
            emit(value)
            launch { block(value) }
        }
    }
}

fun <T> Flow<T>.launchOnEachLatest(block: suspend CoroutineScope.(T) -> Unit) = flow {
    coroutineScope {
        var lastJob: Job? = null
        this@launchOnEachLatest.collect { value ->
            lastJob?.cancelAndJoin()
            emit(value)
            lastJob = launch { block(value) }
        }
    }
}
