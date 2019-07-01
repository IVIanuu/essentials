/*
 * Copyright 2019 Manuel Wrage
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

package com.ivianuu.essentials.util

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import com.ivianuu.scopes.Scope
import com.ivianuu.scopes.ScopeOwner

private const val KEY_LIFECYCLE_OWNER = "lifecycleOwner"

val Scope.lifecycleOwner: LifecycleOwner
    get() = properties.getOrSet(KEY_LIFECYCLE_OWNER) {
        ScopedLifecycleOwner(this)
    }
val Scope.lifecycle: Lifecycle get() = lifecycleOwner.lifecycle

val ScopeOwner.lifecycleOwner: LifecycleOwner get() = scope.lifecycleOwner
val ScopeOwner.lifecycle: Lifecycle get() = scope.lifecycle

private class ScopedLifecycleOwner(scope: Scope) : LifecycleOwner {
    private val _lifecycle = LifecycleRegistry(this)

    init {
        _lifecycle.currentState = Lifecycle.State.RESUMED
        scope.addListener { _lifecycle.currentState = Lifecycle.State.DESTROYED }
    }

    override fun getLifecycle(): Lifecycle = _lifecycle
}