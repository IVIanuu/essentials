package com.ivianuu.injekt

import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass

private val classNames: MutableMap<KClass<*>, String> = ConcurrentHashMap()

internal fun KClass<*>.getFullName() = classNames[this] ?: saveFullName()

private fun KClass<*>.saveFullName(): String {
    val name = this.java.canonicalName
    classNames[this] = name
    return name
}