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

package com.ivianuu.essentials.util.ext

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.ivianuu.essentials.util.lifecycle.SimpleLifecycleObserver
import kotlinx.coroutines.Job

fun Job.cancelledWith(
    owner: LifecycleOwner,
    event: Lifecycle.Event = owner.lifecycle.correspondingEvent()
) = apply {
    owner.lifecycle.addObserver(object : SimpleLifecycleObserver() {
        override fun onAny(owner: LifecycleOwner, e: Lifecycle.Event) {
            if (event == e || e == Lifecycle.Event.ON_DESTROY) cancel()
        }
    })
}

private fun Lifecycle.correspondingEvent(): Lifecycle.Event {
    // get last value based on the current state
    val lastEvent = when (currentState) {
        Lifecycle.State.INITIALIZED -> Lifecycle.Event.ON_CREATE
        Lifecycle.State.CREATED -> Lifecycle.Event.ON_START
        Lifecycle.State.STARTED, Lifecycle.State.RESUMED -> Lifecycle.Event.ON_RESUME
        Lifecycle.State.DESTROYED -> Lifecycle.Event.ON_DESTROY
    }

    return when (lastEvent) {
        Lifecycle.Event.ON_CREATE -> Lifecycle.Event.ON_DESTROY
        Lifecycle.Event.ON_START -> Lifecycle.Event.ON_STOP
        Lifecycle.Event.ON_RESUME -> Lifecycle.Event.ON_PAUSE
        Lifecycle.Event.ON_PAUSE -> Lifecycle.Event.ON_STOP
        Lifecycle.Event.ON_STOP -> Lifecycle.Event.ON_DESTROY
        else -> throw IllegalStateException("Lifecycle has ended! Last event was $lastEvent")
    }
}