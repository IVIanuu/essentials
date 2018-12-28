package com.ivianuu.injekt

import kotlin.reflect.KClass

/**
 * Binds this [Declaration] to [types]
 */
infix fun Declaration<*>.binds(types: Array<KClass<*>>) = apply {
    types.forEach { bind(it) }
}

inline infix fun Declaration<*>.intoSet(options: () -> SetBinding<*>) = apply {
    intoSet(options())
}

infix fun Declaration<*>.intoSet(type: KClass<*>) = intoSet(SetBinding(type, null))

/*
inline infix fun Declaration<*>.intoMap(options: () -> MapBinding) = apply {
    intoMap(options())
}*/

infix fun Declaration<*>.intoClassMap(type: KClass<*>) = apply {
    intoMap(classMapBinding(type, primaryType::class))
}

infix inline fun Declaration<*>.intoStringMap(
    body: () -> Pair<KClass<*>, String>
) = apply {
    val (type, key) = body()
    intoMap(stringMapBinding(type, key))
}