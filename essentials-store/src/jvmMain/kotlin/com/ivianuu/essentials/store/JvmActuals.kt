package com.ivianuu.essentials.store

import kotlin.reflect.*

actual fun <T : Any> KClass<T>.newInstance(): T = java
    .declaredConstructors
    .single { it.parameterTypes.isEmpty() }
    .also { it.isAccessible = true }
    .newInstance() as T
