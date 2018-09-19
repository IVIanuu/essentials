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

import java.util.*
import java.util.concurrent.Executor
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

internal class SingleThreadExecutor : Executor {

    private val lock = ReentrantLock(true)
    private val condition = lock.newCondition()

    private var closed = false

    private val queue = LinkedList<Runnable>()

    private val worker = object : Thread("StateStore-Worker") {
        override fun run() {
            while (running()) {
                next()?.run()
            }
        }

        private fun next(): Runnable? = lock.withLock {
            val next = queue.poll()
            if (next == null && !closed) condition.await()
            next
        }

        private fun running() = lock.withLock { !closed }
    }

    init {
        worker.start()
    }

    override fun execute(command: Runnable): Unit = lock.withLock {
        if (closed) return@withLock
        queue.add(command)
        condition.signalAll()
    }

    fun close(): Unit = lock.withLock {
        closed = true
        condition.signalAll()
    }
}
