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

import com.ivianuu.essentials.ui.core.Stable
import com.ivianuu.scopes.Scope
import java.util.concurrent.ConcurrentHashMap

@Stable
class Properties {

    private val baking = mutableMapOf<Key<*>, Any?>()

    val entries: Map<Key<*>, Any?>
        get() = baking

    val size: Int get() = entries.size

    operator fun <T> get(key: Key<T>): T? = baking[key] as? T

    operator fun <T> set(key: Key<T>, value: T) {
        baking[key] = value
    }

    fun <T> remove(key: Key<T>): T? = baking.remove(key) as? T

    operator fun contains(key: Key<*>): Boolean = baking.containsKey(key)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Properties

        if (baking != other.baking) return false

        return true
    }

    override fun hashCode(): Int = baking.hashCode()

    override fun toString(): String = "Properties($baking)"

    class Key<T>
}

fun propertiesOf(vararg pairs: Pair<Properties.Key<*>, *>): Properties {
    val properties = Properties()
    pairs.forEach { (key, value) ->
        properties[key as Properties.Key<Any?>] = value
    }

    return properties
}

fun <T> Properties.getOrSet(key: Properties.Key<T>, defaultValue: () -> T): T {
    var value = get(key)
    if (value == null) {
        value = defaultValue()
        set(key, value)
    }

    return value as T
}

fun <T> Properties.getOrDefault(key: Properties.Key<T>, defaultValue: () -> T): T {
    var value = get(key)
    if (value == null) {
        value = defaultValue()
    }

    return value as T
}

private val propertiesByScope = ConcurrentHashMap<Scope, Properties>()

val Scope.properties: Properties
    get() = propertiesByScope.getOrPut(this) {
        propertiesOf().also {
            onClose { propertiesByScope -= this }
        }
    }
