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

package com.ivianuu.essentials.permission

import androidx.compose.runtime.Composable
import com.ivianuu.essentials.ui.navigation.Route
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class Permission(private val metadata: Map<Key<*>, Any?>) {

    constructor(vararg metadata: KeyWithValue<*>) : this(
        metadata.associate {
            it.key to it.value
        }
    )

    operator fun <T> get(key: Key<T>): T =
        metadata[key] as? T ?: error("missing value for $key")

    fun <T> getOrNull(key: Key<T>): T? = metadata[key] as? T

    operator fun <T> contains(key: Key<T>): Boolean = metadata.containsKey(key)

    class Key<T>(val name: String) : ReadOnlyProperty<Permission, T> {
        override fun getValue(thisRef: Permission, property: KProperty<*>): T = thisRef[this]
        override fun toString() = name
    }

    companion object
}

data class KeyWithValue<T>(
    val key: Permission.Key<T>,
    val value: T
)

infix fun <T> Permission.Key<T>.withValue(value: T): KeyWithValue<T> =
    KeyWithValue(this, value)

val Permission.Companion.Title by lazy { Permission.Key<String>("Title") }
val Permission.Companion.Desc by lazy { Permission.Key<String>("Desc") }
val Permission.Companion.Icon by lazy { Permission.Key<@Composable () -> Unit>("Icon") }

interface PermissionStateProvider {
    fun handles(permission: Permission): Boolean
    suspend fun isGranted(permission: Permission): Boolean
}

interface PermissionRequestHandler {
    fun handles(permission: Permission): Boolean
    suspend fun request(permission: Permission)
}

interface PermissionRequestRouteFactory {
    fun createRoute(
        request: PermissionRequest
    ): Route
}

data class PermissionRequest(val permissions: List<Permission>)
