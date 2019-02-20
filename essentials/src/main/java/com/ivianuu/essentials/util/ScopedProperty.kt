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

import com.ivianuu.scopes.Scope
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class ScopedProperty<T>(private val initializer: (Scope) -> T) : ReadOnlyProperty<Scope, T> {

    private object UNINITIALIZED

    private var _value: Any? = UNINITIALIZED

    override fun getValue(thisRef: Scope, property: KProperty<*>): T {
        require(!thisRef.isClosed) { "Cannot access field of a closed viewModelScope" }
        if (_value === UNINITIALIZED) {
            _value = initializer(thisRef)
            thisRef.addListener { _value = UNINITIALIZED }
        }

        return _value as T
    }

}

fun <T> scoped(initializer: (Scope) -> T): ScopedProperty<T> = ScopedProperty(initializer)