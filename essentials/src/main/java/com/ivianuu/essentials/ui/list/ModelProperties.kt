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

    private var addedToController = false

    val entries: Map<String, ModelProperty<*>> get() = _entries
    private val _entries = mutableMapOf<String, ModelProperty<*>>()

    private val propertyDelegates = mutableMapOf<String, ModelPropertyDelegate<*>>()

    internal fun addedToController() {
        // force init of all delegates to have consistent equals() and hashCode() results
        propertyDelegates.forEach { it.value.initializeValue() }
        propertyDelegates.clear()

        addedToController = true
    }

    internal fun registerDelegate(delegate: ModelPropertyDelegate<*>) {
        propertyDelegates[delegate.key] = delegate
    }

    fun <T> getPropertyEntry(key: String): ModelProperty<T>? =
        _entries[key] as? ModelProperty<T>?

    fun <T> setProperty(
        property: ModelProperty<T>
    ) {
        check(!addedToController) { "cannot change properties on added models" }
        _entries[property.key] = property
        propertyDelegates.remove(property.key)
    }

    internal fun <T> getOrSetProperty(
        key: String,
        defaultValue: () -> ModelProperty<T>
    ): ModelProperty<T> {
        var property = getPropertyEntry<T>(key)
        if (property == null) {
            property = defaultValue()
            _entries[key] = property
        }

        return property
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ModelProperties) return false

        val entries = _entries
            .filterValues { it.doHash }
            .map { it.value }
        val otherEntries = other._entries
            .filterValues { it.doHash }
            .map { it.value }
        if (entries != otherEntries) return false

        return true
    }

    override fun hashCode(): Int {
        val entries = _entries
            .filterValues { it.doHash }
            .map { it.value }
        return entries.hashCode()
    }

    override fun toString(): String {
        val entries = _entries.map { it.value }
        return entries.toString()
    }

}

fun <T> ModelProperties.getProperty(key: String): T? = getPropertyEntry<T>(key)?.value

fun <T> ModelProperties.requireProperty(key: String): T = getProperty<T>(key)
    ?: error("missing property for key $key")

fun <T> ModelProperties.setProperty(
    key: String,
    value: T,
    doHash: Boolean = true
) {
    setProperty(ModelProperty(key, value, doHash))
}

data class ModelProperty<T>(
    val key: String,
    val value: T,
    val doHash: Boolean = true
)

internal class ModelPropertyDelegate<T>(
    private val model: ListModel<*>,
    val key: String,
    private val doHash: Boolean = true,
    private val defaultValue: () -> T
) : ReadWriteProperty<ListModel<*>, T> {

    init {
        model.properties.registerDelegate(this)
    }

    fun initializeValue() {
        getValueInternal()
    }

    override fun getValue(thisRef: ListModel<*>, property: KProperty<*>): T {
        return getValueInternal()
    }

    override fun setValue(thisRef: ListModel<*>, property: KProperty<*>, value: T) {
        model.properties.setProperty(
            ModelProperty(
                key,
                value,
                doHash
            )
        )
    }

    private fun getValueInternal(): T {
        return model.properties.getOrSetProperty(key) {
            ModelProperty(
                key,
                defaultValue(),
                doHash
            )
        }.value
    }
}