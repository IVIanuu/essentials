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

package com.ivianuu.essentials.injection

import com.ivianuu.injekt.BindingBuilder
import com.ivianuu.injekt.Component
import com.ivianuu.injekt.InjektTrait
import com.ivianuu.injekt.ParametersDefinition
import com.ivianuu.injekt.multibinding.*
import com.ivianuu.injekt.provider.Provider
import kotlin.reflect.KClass

inline fun <reified T> BindingBuilder<T>.bindIntoClassMap(mapName: Any) {
    bindIntoMap(mapName, T::class)
}

fun <T : Any> Component.getClassMap(
    name: Any,
    parameters: ParametersDefinition? = null
): Map<KClass<out T>, T> = getMap(name, parameters)

fun <T : Any> Component.getLazyClassMap(
    name: Any,
    parameters: ParametersDefinition? = null
): Map<KClass<out T>, Lazy<T>> = getLazyMap(name, parameters)

fun <T : Any> Component.getProviderClassMap(
    name: Any,
    defaultParameters: ParametersDefinition? = null
): Map<KClass<out T>, Provider<T>> = getProviderMap(name, defaultParameters)

fun <T : Any> Component.injectClassMap(
    name: Any,
    parameters: ParametersDefinition? = null
): Lazy<Map<KClass<out T>, T>> = injectClassMap(name, parameters)

fun <T : Any> Component.injectLazyClassMap(
    name: Any,
    parameters: ParametersDefinition? = null
): Lazy<Map<KClass<out T>, Lazy<T>>> = injectLazyMap(name, parameters)

fun <T : Any> Component.injectProviderClassMap(
    name: Any,
    defaultParameters: ParametersDefinition? = null
): Lazy<Map<KClass<out T>, Provider<T>>> = injectProviderMap(name, defaultParameters)

fun <T : Any> InjektTrait.getClassMap(
    name: Any,
    parameters: ParametersDefinition? = null
): Map<KClass<out T>, T> = getMap(name, parameters)

fun <T : Any> InjektTrait.getLazyClassMap(
    name: Any,
    parameters: ParametersDefinition? = null
): Map<KClass<out T>, Lazy<T>> = getLazyMap(name, parameters)

fun <T : Any> InjektTrait.getProviderClassMap(
    name: Any,
    defaultParameters: ParametersDefinition? = null
): Map<KClass<out T>, Provider<T>> = getProviderMap(name, defaultParameters)

fun <T : Any> InjektTrait.injectClassMap(
    name: Any,
    parameters: ParametersDefinition? = null
): Lazy<Map<KClass<out T>, T>> = injectMap(name, parameters)

fun <T : Any> InjektTrait.injectLazyClassMap(
    name: Any,
    parameters: ParametersDefinition? = null
): Lazy<Map<KClass<out T>, Lazy<T>>> = injectLazyMap(name, parameters)

fun <T : Any> InjektTrait.injectProviderClassMap(
    name: Any,
    defaultParameters: ParametersDefinition? = null
): Lazy<Map<KClass<out T>, Provider<T>>> = injectProviderMap(name, defaultParameters)