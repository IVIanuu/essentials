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

import com.ivianuu.epoxyprefs.AbstractPreferenceModel
import com.ivianuu.epoxyprefs.dependency
import com.ivianuu.kprefs.ChangeListener
import com.ivianuu.kprefs.Pref
import com.ivianuu.kprefs.common.PrefValueHolder
import com.ivianuu.kprefs.common.valueFor
import kotlin.reflect.KClass

fun <T : Any> AbstractPreferenceModel.Builder<T>.fromPref(pref: Pref<T>) {
    key(pref.key)
    defaultValue(pref.defaultValue)
}

fun <T, S : Any> AbstractPreferenceModel.Builder<S>.fromEnumPref(pref: Pref<T>) where T : Enum<T>, T : PrefValueHolder<S> {
    key(pref.key)
    defaultValue(pref.get().value)
}

fun <T : Any> AbstractPreferenceModel.Builder<*>.dependency(dependency: Pref<T>, value: T) {
    dependency(dependency.key, value, dependency.defaultValue)
}

fun <T, S : Any> AbstractPreferenceModel.Builder<*>.enumDependency(
    dependency: Pref<T>,
    value: T
) where T : Enum<T>, T : PrefValueHolder<S> {
    dependency(dependency.key, value.value, dependency.defaultValue.value)
}

inline fun <reified T, S> Pref<T>.unwrap(): Pref<S> where T : Enum<T>, T : PrefValueHolder<S> =
    unwrap(type = T::class)

fun <T, S> Pref<T>.unwrap(
    type: KClass<T>
): Pref<S> where T : Enum<T>, T : PrefValueHolder<S> {
    val wrapped = this
    return object : Pref<S> {
        override val defaultValue: S
            get() = wrapped.defaultValue.value
        override val isSet: Boolean
            get() = wrapped.isSet
        override val key: String
            get() = wrapped.key

        private val listenerMap = mutableMapOf<ChangeListener<S>, ChangeListener<T>>()

        override fun get(): S = wrapped.get().value

        override fun set(value: S) {
            wrapped.set(type.valueFor(value, wrapped.defaultValue))
        }

        override fun delete() {
            wrapped.delete()
        }

        override fun addListener(listener: ChangeListener<S>) {
            wrapped.addListener(listenerMap.getOrPut(listener) {
                { listener(it.value) }
            })
        }

        override fun removeListener(listener: ChangeListener<S>) {
            listenerMap.remove(listener)?.let { wrapped.removeListener(it) }
        }

    }
}