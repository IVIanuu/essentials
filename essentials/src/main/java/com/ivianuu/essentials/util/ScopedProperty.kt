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

package com.ivianuu.essentials.util

import com.ivianuu.scopes.CloseListener
import com.ivianuu.scopes.Scope
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class ScopedProperty<T>(private val initializer: (Scope) -> T) : ReadWriteProperty<Scope, T> {

    private val valuesByScope =
        mutableMapOf<Scope, Any?>()
    private val listenersByScope =
        mutableMapOf<Scope, RefScopeListener>()

    override fun getValue(thisRef: Scope, property: KProperty<*>): T {
        require(!thisRef.isClosed) { "Cannot access field of a closed scope" }
        var value = valuesByScope.getOrElse(thisRef) { UNINITIALIZED }

        if (value === UNINITIALIZED) {
            value = initializer(thisRef)
            valuesByScope[thisRef] = value
        }

        ensureListenerAdded(thisRef)

        return value as T
    }

    override fun setValue(thisRef: Scope, property: KProperty<*>, value: T) {
        require(!thisRef.isClosed) { "Cannot access field of a closed scope" }
        valuesByScope[thisRef] = value
        ensureListenerAdded(thisRef)
    }

    private fun ensureListenerAdded(scope: Scope) {
        if (!listenersByScope.contains(scope)) {
            listenersByScope[scope] = RefScopeListener(scope)
        }
    }

    private object UNINITIALIZED

    private inner class RefScopeListener(private val scope: Scope) : CloseListener {

        init {
            scope.addListener(this)
        }

        override fun invoke() {
            valuesByScope.remove(scope)
            listenersByScope.remove(scope)
        }
    }
}

fun <T> scoped(initializer: (Scope) -> T): ScopedProperty<T> = ScopedProperty(initializer)