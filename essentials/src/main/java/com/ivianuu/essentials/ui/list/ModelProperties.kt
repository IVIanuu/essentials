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

package com.ivianuu.essentials.ui.list

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Properties of a [ListModel]
 */
class ModelProperties internal constructor() {

    internal var addedToController = false

    private val _entries = mutableMapOf<String, ModelProperty<*>>()

    private var allowSetProperty = false

    internal fun <T> getProperty(key: String): ModelProperty<T>? =
        _entries[key] as? ModelProperty<T>?

    internal fun <T> setProperty(
        property: ModelProperty<T>
    ) {
        check(!addedToController || allowSetProperty) { "cannot change properties on added models" }
        _entries[property.key] = property
    }

    internal fun <T> getPropertyOrSetDefault(
        key: String,
        defaultValue: () -> ModelProperty<T>
    ): ModelProperty<T> {
        var property = getProperty<T>(key)
        if (property == null) {
            property = defaultValue()
            allowSetProperty = true
            setProperty(property)
            allowSetProperty = false
        }

        return property
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ModelProperties) return false

        val entries = _entries.filterValues { it.doHash }
        val otherEntries = other._entries.filterValues { it.doHash }
        if (entries != otherEntries) return false

        return true
    }

    override fun hashCode(): Int {
        val entries = _entries.filterValues { it.doHash }
        return entries.hashCode()
    }

    override fun toString(): String {
        val entries = _entries.filterValues { it.includeInToString }
        return entries.toString()
    }

}

data class ModelProperty<T>(
    val key: String,
    val value: T,
    val doHash: Boolean = true,
    val includeInToString: Boolean = true
) {
    override fun toString(): String {
        return value.toString()
    }
}

fun <T> ListModel<*>.property(
    key: String? = null,
    doHash: Boolean = true,
    includeInToString: Boolean = true,
    defaultValue: (String) -> T
): ReadWriteProperty<ListModel<*>, T> = ModelPropertyDelegate(
    key, doHash, includeInToString, defaultValue
)

fun <T> ListModel<*>.requiredProperty(
    key: String? = null,
    doHash: Boolean = true,
    includeInToString: Boolean = true
): ReadWriteProperty<ListModel<*>, T> = ModelPropertyDelegate(
    key, doHash, includeInToString
) {
    error("missing property $it use optionalProperty() for optional ones")
}

fun <T> ListModel<*>.optionalProperty(
    key: String? = null,
    doHash: Boolean = true,
    includeInToString: Boolean = true
): ReadWriteProperty<ListModel<*>, T?> =
    ModelPropertyDelegate(key, doHash, includeInToString) { null }

private class ModelPropertyDelegate<T>(
    private val key: String? = null,
    private val doHash: Boolean = true,
    private val includeInToString: Boolean = true,
    private val defaultValue: (String) -> T
) : ReadWriteProperty<ListModel<*>, T> {

    private var realKey: String? = key

    override fun getValue(thisRef: ListModel<*>, property: KProperty<*>): T {
        val key = getRealKey(thisRef, property)
        return thisRef.properties.getPropertyOrSetDefault<T>(key) {
            ModelProperty(
                key,
                defaultValue(key),
                doHash,
                includeInToString
            )
        }.value
    }

    override fun setValue(thisRef: ListModel<*>, property: KProperty<*>, value: T) {
        thisRef.properties.setProperty(
            ModelProperty(
                getRealKey(thisRef, property),
                value,
                doHash,
                includeInToString
            )
        )
    }

    private fun getRealKey(thisRef: ListModel<*>, property: KProperty<*>): String {
        if (realKey == null) {
            realKey = key ?: "${thisRef.javaClass.name}.${property.name}"
        }

        return realKey!!
    }
}