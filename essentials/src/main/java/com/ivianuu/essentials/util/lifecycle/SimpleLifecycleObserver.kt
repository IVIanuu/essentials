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
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent

/**
 * A simple lifecycle observer
 */
open class SimpleLifecycleObserver : LifecycleObserver {

    @OnLifecycleEvent(Lifecycle.Event.ON_ANY)
    open fun onAny(owner: LifecycleOwner, event: Lifecycle.Event) {
        when (event) {
            Lifecycle.Event.ON_CREATE -> onCreate(owner)
            Lifecycle.Event.ON_START -> onStart(owner)
            Lifecycle.Event.ON_RESUME -> onResume(owner)
            Lifecycle.Event.ON_PAUSE -> onPause(owner)
            Lifecycle.Event.ON_STOP -> onStop(owner)
            Lifecycle.Event.ON_DESTROY -> onDestroy(owner)
            Lifecycle.Event.ON_ANY -> {
            } // ignore
        }
    }

    open fun onCreate(owner: LifecycleOwner) {
    }

    open fun onStart(owner: LifecycleOwner) {
    }

    open fun onResume(owner: LifecycleOwner) {
    }

    open fun onPause(owner: LifecycleOwner) {
    }

    open fun onStop(owner: LifecycleOwner) {
    }

    open fun onDestroy(owner: LifecycleOwner) {
    }
}