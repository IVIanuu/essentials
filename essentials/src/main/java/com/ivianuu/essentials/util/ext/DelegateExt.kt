package com.ivianuu.essentials.util.ext

import java.lang.reflect.Field
import kotlin.reflect.KClass

private const val DELEGATE_NAME = "\$\$delegate_"

private val delegateCache = mutableMapOf<KClass<*>, Set<Field>>()

fun <T : Any> Any.bindDelegate(clazz: KClass<T>) = unsafeLazy { delegate(clazz) }

fun <T : Any> Any.delegateOrNull(clazz: KClass<T>) = try {
    delegate(clazz)
} catch (e: Exception) {
    null
}

@Suppress("UNCHECKED_CAST")
fun <T : Any> Any.delegate(clazz: KClass<T>) =
    delegatesFields()
        .first { it.type.isAssignableFrom(clazz.java) }
        .get(this) as T

fun Any.bindDelegates() = unsafeLazy { delegates() }

fun Any.delegates() =
    delegatesFields()
        .mapNotNull {
            try {
                it.get(this)
            } catch (e: Exception) {
                null
            }
        }

private fun Any.delegatesFields() =
    delegateCache.getOrPut(this::class) { collectDelegates(this::class) }

private fun collectDelegates(clazz: KClass<*>) = try {
    val delegates = mutableSetOf<Field>()

    // process all classes
    var currentClazz: Class<*>? = clazz.java
    try {
        while (currentClazz != null) {
            // collect delegate fields
            try {
                var count = 0

                while (true) {
                    delegates.add(currentClazz.getDeclaredField(DELEGATE_NAME + count))
                    count++
                }
            } catch (e: Exception) {
            }

            currentClazz = currentClazz.superclass
        }
    } catch (e: Exception) {
    }

    delegates
        .asSequence()
        .filter {
            try {
                it.isAccessible = true
                true
            } catch (e: Exception) {
                false
            }
        }
        .toSet()
} catch (e: Exception) {
    emptySet<Field>()
}
