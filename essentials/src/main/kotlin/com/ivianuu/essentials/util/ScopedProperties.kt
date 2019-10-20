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

import com.ivianuu.scopes.Scope
import java.util.concurrent.ConcurrentHashMap

class Properties {

    private val properties = mutableMapOf<String, Any?>()

    val entries: Map<String, Any?>
        get() = properties

    operator fun <T> get(key: String): T? = properties[key] as? T

    operator fun <T> set(key: String, value: T) {
        properties[key] = value as Any?
    }

    fun <T> remove(key: String): T? = properties.remove(key) as? T

    operator fun contains(key: String): Boolean = properties.containsKey(key)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Properties

        if (properties != other.properties) return false

        return true
    }

    override fun hashCode(): Int = properties.hashCode()

}

val Properties.size: Int get() = entries.size

fun <T> Properties.getOrSet(key: String, defaultValue: () -> T): T {
    var value = get<T>(key)
    if (value == null) {
        value = defaultValue()
        set(key, value as Any)
    }

    return value
}

fun <T> Properties.getOrDefault(key: String, defaultValue: () -> T): T {
    var value = get<T>(key)
    if (value == null) {
        value = defaultValue()
    }

    return value as T
}

private val propertiesByScope = ConcurrentHashMap<Scope, Properties>()

val Scope.properties: Properties
    get() = propertiesByScope.getOrPut(this) {
        Properties().also {
            onClose { propertiesByScope -= this }
        }
    }