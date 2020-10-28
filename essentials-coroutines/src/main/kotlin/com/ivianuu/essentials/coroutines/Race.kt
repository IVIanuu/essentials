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
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.selects.select

suspend fun <T> raceOf(vararg racers: suspend CoroutineScope.() -> T): T {
    require(racers.isNotEmpty()) { "A race needs racers." }
    return race {
        racers.forEach {
            launchRacer(it)
        }
    }
}

interface RacingScope<in T> : CoroutineScope {
    fun launchRacer(block: suspend CoroutineScope.() -> T)
}

suspend fun <T> race(
    @BuilderInference
    block: suspend RacingScope<T>.() -> Unit
): T = coroutineScope {
    val racers = mutableListOf<Deferred<T>>()
    select<T> {
        val job = Job(parent = coroutineContext[Job])
        val racingScope = object : RacingScope<T>, CoroutineScope by this@coroutineScope {
            var raceWon = false
            override fun launchRacer(block: suspend CoroutineScope.() -> T) {
                if (raceWon) return // A racer already completed.
                async(block = block).also { racerAsync ->
                    racers += racerAsync
                    if (raceWon) { // A racer just completed on another thread, cancel.
                        racerAsync.cancel()
                    }
                }.onAwait { resultOfWinner: T ->
                    raceWon = true
                    job.cancel()
                    var i = 0
                    // Since launchRacerInternal might be called on multiple threads concurrently,
                    //  we don't use a forEach loop, but a while loop that is additions tolerant.
                    while (i <= racers.lastIndex) {
                        val deferred = racers[i]
                        deferred.cancel()
                        i++
                    }
                    return@onAwait resultOfWinner
                }
            }
        }
        launch(job, start = CoroutineStart.UNDISPATCHED) {
            racingScope.block()
        }
    }
}
