package com.ivianuu.injekt

import kotlin.reflect.KClass

/**
 * Unique key per dependency declaration based either on the class or name of the dependency.
 */
sealed class Key {

    data class ClassKey<T : Any>(val clazz: KClass<T>) : Key()

    data class NameKey(val name: String) : Key()

    companion object {

        fun <T : Any> of(clazz: KClass<T>, name: String? = null): Key =
            when (name) {
                null -> ClassKey(clazz)
                else -> NameKey(name)
            }

    }

}
