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

package com.ivianuu.essentials.util.lifecycle

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.ivianuu.essentials.util.ext.mainThread
import java.util.*

/**
 * Dispatches events to consumers and buffers them while no one is subscribed
 */
open class LiveEvent<T> {

    private val consumers = mutableSetOf<ConsumerEntry<T>>()

    private val pendingEvents = LinkedList<T>()

    val hasActiveConsumers get() = consumers.any { it.isActive }

    fun consume(owner: LifecycleOwner, consumer: (T) -> Unit) {
        consumers.add(ConsumerEntry(owner, consumer))
        dispatchPendingEvents()
    }

    fun removeConsumer(consumer: (T) -> Unit) {
        consumers.removeAll { it.consumer == consumer }
    }

    protected open fun offer(event: T) {
        pendingEvents.add(event)
        dispatchPendingEvents()
    }

    private fun dispatchPendingEvents() = mainThread {
        if (consumers.any { it.isActive }) {
            while (pendingEvents.isNotEmpty()) {
                val event = pendingEvents.poll()
                consumers
                    .filter { it.isActive }
                    .forEach { it(event) }
            }
        }
    }

    private class ConsumerEntry<T>(
        val owner: LifecycleOwner,
        val consumer: (T) -> Unit
    ) {
        val isActive get() = owner.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)

        operator fun invoke(event: T) {
            consumer.invoke(event)
        }
    }
}

/**
 * Mutable live event
 */
open class MutableLiveEvent<T> : LiveEvent<T>() {

    public override fun offer(event: T) {
        super.offer(event)
    }

}

fun <T> mutableLiveEvent() = MutableLiveEvent<T>()