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

package com.ivianuu.essentials.ui.mvrx.lifecycle

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.ivianuu.essentials.util.SimpleLifecycleObserver

internal class LifecycleLazy<T>(
    owner: LifecycleOwner,
    private val event: Lifecycle.Event = Lifecycle.Event.ON_CREATE,
    initializer: () -> T
) : Lazy<T> {

    private object UNINITIALIZED_VALUE

    private var initializer: (() -> T)? = initializer
    @Volatile private var _value: Any? =
        UNINITIALIZED_VALUE
    // final field is required to enable safe publication of constructed instance
    private val lock = this

    init {
        owner.lifecycle.addObserver(object : SimpleLifecycleObserver() {
            override fun onAny(owner: LifecycleOwner, event: Lifecycle.Event) {
                if (this@LifecycleLazy.event == event) {
                    if (!isInitialized()) value
                    owner.lifecycle.removeObserver(this)
                }
            }
        })
    }

    @Suppress("LocalVariableName")
    override val value: T
        get() {
            val _v1 = _value
            if (_v1 !== UNINITIALIZED_VALUE) {
                @Suppress("UNCHECKED_CAST")
                return _v1 as T
            }

            return synchronized(lock) {
                val _v2 = _value
                if (_v2 !== UNINITIALIZED_VALUE) {
                    @Suppress("UNCHECKED_CAST") (_v2 as T)
                } else {
                    val typedValue = initializer!!()
                    _value = typedValue
                    initializer = null
                    typedValue
                }
            }
        }

    override fun isInitialized(): Boolean = _value !== UNINITIALIZED_VALUE

    override fun toString(): String =
        if (isInitialized()) value.toString() else "Lazy value not initialized yet."
}

@PublishedApi
internal fun <T> LifecycleOwner.lifecycleLazy(
    initializer: () -> T
): Lazy<T> = LifecycleLazy(this, initializer = initializer)