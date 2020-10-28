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

import kotlinx.coroutines.delay
import kotlin.time.Duration
import kotlin.time.milliseconds
import kotlin.time.seconds

suspend fun <T> retry(
    times: Int = Int.MAX_VALUE,
    initialDelay: Duration = 100.milliseconds,
    maxDelay: Duration = 1.seconds,
    factor: Double = 2.0,
    predicate: suspend (Throwable) -> Boolean = { true },
    block: suspend () -> T,
): T {
    var currentDelay = initialDelay
    repeat(times - 1) {
        try {
            return block()
        } catch (t: Throwable) {
            if (!predicate(t)) throw RuntimeException(t)
        }
        delay(currentDelay.toLongMilliseconds()) // todo remove toLongMilliseconds()
        currentDelay = (currentDelay * factor).coerceAtMost(maxDelay)
    }

    return block()
}
