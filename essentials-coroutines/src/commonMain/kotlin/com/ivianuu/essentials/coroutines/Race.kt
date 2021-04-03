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

import kotlin.experimental.ExperimentalTypeInference
import kotlinx.coroutines.*
import kotlinx.coroutines.selects.select

suspend fun <T> raceOf(vararg racers: suspend () -> T): T {
    require(racers.isNotEmpty()) { "A race needs racers." }
    return race {
        racers.forEach {
            launchRacer { it() }
        }
    }
}

suspend fun <T> Iterable<suspend () -> T>.race(): T = race {
    forEach {
        launchRacer { it() }
    }
}

suspend fun <T> race(@BuilderInference block: suspend RacingScope<T>.() -> Unit): T = coroutineScope {
    val racers = mutableListOf<Deferred<T>>()
    select {
        val scopeBlockJob = childJob()
        val racingScope = object : RacingScope<T>, CoroutineScope by this@coroutineScope {
            var finished = false
            override fun launchRacer(block: suspend CoroutineScope.() -> T) {
                if (finished) return
                synchronized(this@coroutineScope) {
                    if (finished) return
                    async { block() }.also { racers += it }
                }.onAwait { result ->
                    result.also {
                        synchronized(this@coroutineScope) {
                            if (!finished) {
                                finished = true
                                scopeBlockJob.cancel()
                                racers.forEach { it.cancel() }
                            }
                        }
                    }
                }
            }
        }
        launch(context = scopeBlockJob, start = CoroutineStart.UNDISPATCHED) {
            racingScope.block()
        }
    }
}

interface RacingScope<in T> : CoroutineScope {
    fun launchRacer(block: suspend CoroutineScope.() -> T)
}
