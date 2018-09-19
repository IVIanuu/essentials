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

package com.ivianuu.essentials.sample.ui.state

import java.util.concurrent.Executor
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

internal class StateSubject<T>(private val executor: Executor) {

    private val listeners = mutableListOf<StateListener<T>>()

    private var state: T? = null

    private var lock = ReentrantLock()

    fun addStateListener(listener: StateListener<T>): Unit = lock.withLock {
        listeners.add(listener)

        // notify the current state
        state?.let { state ->
            executor.execute { listener(state) }
        }
    }

    fun removeStateListener(listener: StateListener<T>): Unit = lock.withLock {
        listeners.remove(listener)
    }

    fun publish(state: T): Unit = lock.withLock {
        // safe the state
        this.state = state

        // create a snapshot of the current listeners
        val listeners = listeners.toList()

        // notify listeners
        executor.execute { listeners.forEach { it(state) } }
    }
}