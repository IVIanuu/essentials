package com.ivianuu.essentials.injection

import com.ivianuu.injekt.Module
import com.ivianuu.injekt.ParamsDefinition
import com.ivianuu.injekt.get
import kotlin.reflect.KClass

inline fun <reified T : Any> Module.lazy(
    name: String? = null,
    noinline params: ParamsDefinition? = null
) = lazy(T::class, name, params)

fun <T : Any> Module.lazy(
    type: KClass<T>,
    name: String? = null,
    params: ParamsDefinition? = null
): Lazy<T> = kotlin.lazy { get(type, name, params) }