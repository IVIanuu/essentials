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

import com.ivianuu.epoxyprefs.AbstractPreferenceModel
import com.ivianuu.epoxyprefs.dependency
import com.ivianuu.kprefs.Pref
import com.ivianuu.kprefs.common.PrefValueHolder

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