/*
 * Copyright 2020 Manuel Wrage
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

package com.ivianuu.essentials.datastore.android.prefs

import kotlin.reflect.typeOf

open class Prefs(internal open val map: Map<Key<*>, Any>) {
    fun <T : Any> getOrNull(key: Key<T>): T? = map[key] as? T

    fun asMap() = map

    data class Key<T : Any> @PublishedApi internal constructor(val name: String)

    class Pair<T : Any>(val key: Key<T>, val value: T)

    override fun equals(other: Any?): Boolean {
        if (other !is Prefs) return false
        if (map != other.map) return false
        return true
    }

    override fun hashCode(): Int = map.hashCode()

    override fun toString(): String = "Prefs($map)"
}

operator fun <T : Any> Prefs.get(key: Prefs.Key<T>): T =
    getOrNull(key) ?: error("No value found for $key")

inline fun <T : Any> Prefs.getOrElse(key: Prefs.Key<T>, defaultValue: () -> T): T =
    getOrNull(key) ?: defaultValue()

operator fun <T : Any> Prefs.contains(key: Prefs.Key<T>): Boolean = getOrNull(key) != null

class MutablePrefs(override val map: MutableMap<Key<*>, Any>) : Prefs(map) {
    operator fun <T : Any> set(key: Key<T>, value: T) {
        map[key] = value
    }

    operator fun <T : Any> minusAssign(key: Key<T>) {
        map -= key
    }

    fun clear() {
        map.clear()
    }
}

fun mutablePrefsOf(vararg pairs: Prefs.Pair<*>): Prefs = MutablePrefs(
    pairs.associateTo(mutableMapOf()) { it.key to it.value }
)

inline fun <reified T : Any> prefKeyOf(name: String): Prefs.Key<T> {
    return when (T::class) {
        Int::class -> Prefs.Key(name)
        String::class -> Prefs.Key(name)
        Boolean::class -> Prefs.Key(name)
        Float::class -> Prefs.Key(name)
        Long::class -> Prefs.Key(name)
        Double::class -> Prefs.Key(name)
        Set::class -> {
            val type = typeOf<T>()
            if (type.arguments.first().type?.classifier != String::class) {
                error("Only String sets are currently supported.")
            } else Prefs.Key(name)
        }
        else -> error("Type not supported: ${T::class.java}")
    }
}

infix fun <T : Any> Prefs.Key<T>.to(value: T) = Pair(this, value)
