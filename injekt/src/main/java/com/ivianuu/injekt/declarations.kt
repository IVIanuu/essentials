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

inline infix fun <reified T : Any> Declaration<T>.intoSet(type: KClass<T>) =
    intoSet(SetBinding(type, null))

/*
inline infix fun Declaration<*>.intoMap(options: () -> MapBinding) = apply {
    intoMap(options())
}*/

inline infix fun <reified T : Any> Declaration<*>.intoClassMap(type: KClass<T>) = apply {
    intoMap(classMapBinding<T>(primaryType::class))
}

inline infix fun <reified T : Any> Declaration<*>.intoClassMap(body: () -> Pair<KClass<T>, String?>) =
    apply {
        val (_, name) = body()
        intoMap(classMapBinding<T>(primaryType::class, name))
    }

inline infix fun <reified T : Any> Declaration<*>.intoStringMap(
    body: () -> Pair<KClass<T>, String>
) = apply {
    val (type, key) = body()
    intoMap(stringMapBinding<T>(key))
}