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

import com.ivianuu.injekt.BindingContext
import com.ivianuu.injekt.Component
import com.ivianuu.injekt.InjektTrait
import com.ivianuu.injekt.Module
import com.ivianuu.injekt.ParametersDefinition
import com.ivianuu.injekt.Provider
import com.ivianuu.injekt.multibinding.MapBinding
import com.ivianuu.injekt.multibinding.bindIntoMap
import com.ivianuu.injekt.multibinding.getLazyMap
import com.ivianuu.injekt.multibinding.getMap
import com.ivianuu.injekt.multibinding.getProviderMap
import com.ivianuu.injekt.multibinding.injectLazyMap
import com.ivianuu.injekt.multibinding.injectMap
import com.ivianuu.injekt.multibinding.injectProviderMap
import kotlin.reflect.KClass

inline infix fun <reified T> BindingContext<T>.bindIntoClassMap(mapName: String): BindingContext<T> =
    bindIntoMap(MapBinding(mapName, T::class))

inline fun <reified T> BindingContext<T>.bindIntoClassMap(
    mapName: String,
    override: Boolean = false
): BindingContext<T> = bindIntoMap(MapBinding(mapName, T::class, override))

inline fun <reified T> Module.bindIntoClassMap(
    mapName: String,
    override: Boolean = false,
    implementationName: String? = null
) {
    bindIntoMap<T>(mapName, T::class, override, implementationName)
}

fun <T : Any> Component.getClassMap(
    name: String,
    parameters: ParametersDefinition? = null
): Map<KClass<out T>, T> = getMap(name, parameters)

fun <T : Any> Component.getLazyClassMap(
    name: String,
    parameters: ParametersDefinition? = null
): Map<KClass<out T>, Lazy<T>> = getLazyMap(name, parameters)

fun <T : Any> Component.getProviderClassMap(
    name: String,
    defaultParameters: ParametersDefinition? = null
): Map<KClass<out T>, Provider<T>> = getProviderMap(name, defaultParameters)

fun <T : Any> Component.injectClassMap(
    name: String,
    parameters: ParametersDefinition? = null
): Lazy<Map<KClass<out T>, T>> = injectClassMap(name, parameters)

fun <T : Any> Component.injectLazyClassMap(
    name: String,
    parameters: ParametersDefinition? = null
): Lazy<Map<KClass<out T>, Lazy<T>>> = injectLazyMap(name, parameters)

fun <T : Any> Component.injectProviderClassMap(
    name: String,
    defaultParameters: ParametersDefinition? = null
): Lazy<Map<KClass<out T>, Provider<T>>> = injectProviderMap(name, defaultParameters)

fun <T : Any> InjektTrait.getClassMap(
    name: String,
    parameters: ParametersDefinition? = null
): Map<KClass<out T>, T> = getMap(name, parameters)

fun <T : Any> InjektTrait.getLazyClassMap(
    name: String,
    parameters: ParametersDefinition? = null
): Map<KClass<out T>, Lazy<T>> = getLazyMap(name, parameters)

fun <T : Any> InjektTrait.getProviderClassMap(
    name: String,
    defaultParameters: ParametersDefinition? = null
): Map<KClass<out T>, Provider<T>> = getProviderMap(name, defaultParameters)

fun <T : Any> InjektTrait.injectClassMap(
    name: String,
    parameters: ParametersDefinition? = null
): Lazy<Map<KClass<out T>, T>> = injectMap(name, parameters)

fun <T : Any> InjektTrait.injectLazyClassMap(
    name: String,
    parameters: ParametersDefinition? = null
): Lazy<Map<KClass<out T>, Lazy<T>>> = injectLazyMap(name, parameters)

fun <T : Any> InjektTrait.injectProviderClassMap(
    name: String,
    defaultParameters: ParametersDefinition? = null
): Lazy<Map<KClass<out T>, Provider<T>>> = injectProviderMap(name, defaultParameters)