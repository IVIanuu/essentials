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

package com.ivianuu.essentials.permission

import androidx.ui.graphics.Image
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

data class Metadata internal constructor(
    private val data: Map<Key<*>, Any?> = mutableMapOf()
) {
    operator fun <T> get(key: Key<T>): T =
        data[key] as? T ?: error("missing value for $key")

    fun <T> getOrNull(key: Key<T>): T? = data[key] as? T

    fun <T> contains(key: Key<T>): Boolean = data.containsKey(key)

    operator fun plus(other: Metadata): Metadata = Metadata(data + other.data)

    class Key<T>(val name: String) : ReadOnlyProperty<Metadata, T> {
        override fun getValue(thisRef: Metadata, property: KProperty<*>): T = thisRef[this]

        override fun toString() = "Metadata.Key($name)"
    }
}

fun metadataOf(
    vararg pairs: Pair<Metadata.Key<*>, Any?>
): Metadata {
    return Metadata(
        data = pairs.associateBy { it.first }
            .mapValues { it.value.second }
    )
}

object MetadataKeys

val MetadataKeys.Title by lazy { Metadata.Key<String>("Title") }
val MetadataKeys.Desc by lazy { Metadata.Key<String>("Desc") }
val MetadataKeys.Icon by lazy { Metadata.Key<Image>("Image") }
