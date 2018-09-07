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
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * A lazy property that gets cleaned up when the fragment is destroyed.
 *
 * Accessing this variable in a destroyed fragment will throw NPE.
 */
class AutoClearedValue<T : Any>(
    val owner: LifecycleOwner,
    val event: Lifecycle.Event = Lifecycle.Event.ON_DESTROY
) : ReadWriteProperty<Any, T> {

    private var value: T? = null

    init {
        owner.lifecycle.addObserver(object : SimpleLifecycleObserver() {
            override fun onAny(owner: LifecycleOwner, event: Lifecycle.Event) {
                if (this@AutoClearedValue.event == event) {
                    value = null
                }
            }
        })
    }

    override fun getValue(thisRef: Any, property: KProperty<*>): T {
        return value ?: throw IllegalStateException(
            "should never call auto-cleared-value get when it might not be available"
        )
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: T) {
        this.value = value
    }
}

@JvmName("autoCleared2")
fun <T : Any> LifecycleOwner.autoCleared(
    event: Lifecycle.Event = Lifecycle.Event.ON_DESTROY
) = autoCleared<T>(this, event)

fun <T : Any> autoCleared(
    owner: LifecycleOwner,
    event: Lifecycle.Event = Lifecycle.Event.ON_DESTROY
) = AutoClearedValue<T>(owner, event)