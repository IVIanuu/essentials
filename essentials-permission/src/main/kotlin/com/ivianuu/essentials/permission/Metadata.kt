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

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

data class Metadata internal constructor(
    private val data: MutableMap<Key<*>, Any?>
) {
    internal operator fun <T> get(dataElement: Key<T>): T =
        data[dataElement] as? T ?: error("missing value for $dataElement")

    internal operator fun <T> set(key: Key<T>, value: T) {
        data[key] = value
    }

    class Key<T> : ReadWriteProperty<Metadata, T> {
        override fun setValue(thisRef: Metadata, property: KProperty<*>, value: T) {
            thisRef[this] = value
        }

        override fun getValue(thisRef: Metadata, property: KProperty<*>): T = thisRef[this]
    }
}

private val TitleKey = Metadata.Key<String>()
val Metadata.title by TitleKey

private val DescKey = Metadata.Key<String>()
val Metadata.desc by DescKey