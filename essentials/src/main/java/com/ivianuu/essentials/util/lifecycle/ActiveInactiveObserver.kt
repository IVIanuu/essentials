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

import androidx.lifecycle.GenericLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner

open class ActiveInactiveObserver : GenericLifecycleObserver {

    private var isActive = false

    override fun onStateChanged(owner: LifecycleOwner, event: Lifecycle.Event) {
        if (owner.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
            if (!isActive) {
                isActive = true
                onActive(owner)
            }
        } else {
            if (isActive) {
                isActive = false
                onInactive(owner)
            }
        }
    }

    protected open fun onActive(owner: LifecycleOwner) {

    }

    protected open fun onInactive(owner: LifecycleOwner) {
    }
}

fun lifecycleAwareComponent(
    owner: LifecycleOwner,
    onInactive: ((LifecycleOwner) -> Unit)? = null,
    onActive: ((LifecycleOwner) -> Unit)? = null
) {
    owner.lifecycle.addObserver(object : ActiveInactiveObserver() {
        override fun onActive(owner: LifecycleOwner) {
            super.onActive(owner)
            onActive?.invoke(owner)
        }

        override fun onInactive(owner: LifecycleOwner) {
            super.onInactive(owner)
            onInactive?.invoke(owner)
        }
    })
}