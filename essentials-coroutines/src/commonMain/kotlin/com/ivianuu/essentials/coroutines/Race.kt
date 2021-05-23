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
import kotlinx.coroutines.selects.*
import kotlin.coroutines.*

@Deprecated(message = "A race needs racers", level = DeprecationLevel.ERROR)
suspend fun <T> race(context: CoroutineContext = EmptyCoroutineContext) {
}

suspend fun <T> race(
  vararg racers: suspend CoroutineScope.() -> T,
  context: CoroutineContext = EmptyCoroutineContext
): T = coroutineScope {
  select {
    val allRacers = racers.map { racer ->
      async(context = context, block = racer)
    }
    allRacers.forEach { deferredRacer ->
      deferredRacer.onAwait { result ->
        allRacers.forEach { it.cancel() }
        result
      }
    }
  }
}
