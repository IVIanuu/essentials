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

import android.os.Looper
import java.util.*
import java.util.concurrent.locks.ReentrantLock
import kotlin.collections.ArrayList
import kotlin.concurrent.withLock

/**
 * The actual implementation of a [StateStore]
 */
class RealStateStore<T>(
    initialState: T
) : StateStore<T> {

    private var state = initialState

    private val executor = SingleThreadExecutor()

    private var closed = false
    private var isStarting = false
    private var isExecuting = false

    private val stateSubject = StateSubject<T>(LooperExecutor(Looper.getMainLooper()))

    private val lock = ReentrantLock()

    private val pendingActions = LinkedList<Action<T>>()
    private val pendingReducers = LinkedList<Reducer<T>>()

    init {
        stateSubject.publish(initialState)
    }

    override fun withState(action: Action<T>) = lock.withLock {
        if (closed) return@withLock
        pendingActions.add(action)
        notifyWork()
    }

    override fun setState(reducer: Reducer<T>) = lock.withLock {
        if (closed) return@withLock
        pendingReducers.add(reducer)
        notifyWork()
    }

    override fun addStateListener(listener: StateListener<T>) = lock.withLock {
        if (closed) return@withLock
        stateSubject.addStateListener(listener)
    }

    override fun removeStateListener(listener: StateListener<T>) = lock.withLock {
        if (closed) return@withLock
        stateSubject.removeStateListener(listener)
    }

    override fun close() = lock.withLock {
        if (closed) return@withLock
        closed = true
        executor.close()
    }

    private fun notifyWork() = lock.withLock {
        if (closed) return@withLock
        if (isStarting) return@withLock
        if (isExecuting) return@withLock

        isStarting = true

        executor.execute { execute() }
    }

    private fun execute() = lock.withLock {
        if (closed) return@withLock
        if (isExecuting) return@withLock

        isExecuting = true
        isStarting = false

        while (running()) {
            cycle()
        }
    }

    private fun running() = lock.withLock {
        if (closed) return@withLock false

        val hasWork = pendingActions.isNotEmpty() || pendingReducers.isNotEmpty()

        if (!hasWork) {
            isExecuting = false
            return@withLock false
        }

        return@withLock true
    }

    private fun cycle() {
        val previousState = state

        // collect reducers
        val reducers = ArrayList(pendingReducers)
        pendingReducers.clear()

        // collect actions
        val actions = ArrayList(pendingActions)
        pendingActions.clear()

        // apply all reducers
        reducers
            .fold(state) { state, reducer -> state.reducer() }
            .also { state = it }

        // invoke gets
        actions.forEach {
            if (!closed) {
                it(state)
            }
        }

        // notify change
        if (previousState != state) {
            stateSubject.publish(state)
        }
    }
}