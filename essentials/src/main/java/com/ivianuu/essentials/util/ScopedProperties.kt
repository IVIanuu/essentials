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
import java.util.concurrent.ConcurrentHashMap

interface Properties {

    val entries: Map<String, Any?>

    fun <T> get(key: String): T?

    fun <T> set(key: String, value: T)

    fun contains(key: String): Boolean

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

fun Properties(): Properties = RealProperties()

private class RealProperties : Properties {

    private val properties = ConcurrentHashMap<String, Any?>()

    override val entries: Map<String, Any?>
        get() = properties

    override fun <T> get(key: String): T? = properties[key] as? T

    override fun <T> set(key: String, value: T) {
        properties[key] = value as Any?
    }

    override fun contains(key: String) = properties.contains(key)

}

private val propertiesByScope =
    ConcurrentHashMap<Scope, Properties>()

val Scope.properties: Properties
    get() = propertiesByScope.getOrPut(this) {
        check(!isClosed) { "cannot access properties on closed scopes" }
        Properties().also {
            addListener { propertiesByScope.remove(this) }
        }
    }