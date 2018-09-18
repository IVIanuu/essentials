package com.ivianuu.essentials.util.ext

import java.lang.reflect.Field
import kotlin.reflect.KClass

private const val DELEGATE_NAME = "\$\$delegate_"

private val delegateCache = mutableMapOf<Class<*>, Set<Field>>()

fun <T : Any> Any.bindDelegate(clazz: KClass<T>) = unsafeLazy { delegate(clazz) }

inline fun <reified T : Any> Any.bindDelegate() = bindDelegate(T::class)

@Suppress("UNCHECKED_CAST")
fun <T : Any> Any.delegate(clazz: KClass<T>) =
    delegatesFields()
        .firstOrNull { it.type.isAssignableFrom(clazz.java) }
        ?.get(this) as? T
        ?: throw IllegalArgumentException("no delegate found which matches $clazz")

inline fun <reified T : Any> Any.delegate() = delegate(T::class)

fun <T : Any> Any.delegateOrNull(clazz: KClass<T>) = tryOrNull { delegate(clazz) }

inline fun <reified T : Any> Any.delegateOrNull() = delegateOrNull(T::class)

fun Any.bindDelegates() = unsafeLazy { delegates() }

fun Any.delegates() =
    delegatesFields().mapNotNull { tryOrNull { it.get(this) } }

private fun Any.delegatesFields() =
    delegateCache.getOrPut(javaClass) { collectDelegatesRecursively(javaClass) }

private fun collectDelegatesRecursively(clazz: Class<*>) = try {
    val delegates = mutableSetOf<Field>()

    // process all classes
    var currentClazz: Class<*>? = clazz
    try {
        while (currentClazz != null) {
            delegates.addAll(collectDelegatesForClass(currentClazz))
            currentClazz = currentClazz.superclass

            // do not process objects
            if (currentClazz == Any::class.java) break
        }
    } catch (e: Exception) {
    }

    delegates
} catch (e: Exception) {
    emptySet<Field>()
}

private fun collectDelegatesForClass(clazz: Class<*>) = try {
    delegateCache.getOrPut(clazz) {
        val delegates = mutableSetOf<Field>()

        // collect delegate fields
        try {
            var count = 0
            while (true) {
                delegates.add(clazz.getDeclaredField(DELEGATE_NAME + count))
                count++
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
    }
} catch (e: Exception) {
    emptySet<Field>()
}