package com.ivianuu.essentials.injection

import com.ivianuu.injekt.Module
import com.ivianuu.injekt.ParamsDefinition
import com.ivianuu.injekt.get
import kotlin.reflect.KClass

typealias Provider<T> = (params: ParamsDefinition?) -> T

inline fun <reified T : Any> Module.provider(
    name: String? = null
) = provider(T::class, name)

fun <T : Any> Module.provider(
    type: KClass<T>,
    name: String? = null
): Provider<T> = { get(type, name, it) }