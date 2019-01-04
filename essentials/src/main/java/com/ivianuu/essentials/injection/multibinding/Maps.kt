package com.ivianuu.essentials.injection.multibinding

import com.ivianuu.injekt.*
import java.util.*
import kotlin.reflect.KClass

const val KEY_MAP_BINDINGS = "mapBindings"

/**
 * Adds a definition for a map of [K, T] with the name [mapName]
 */
fun <K : Any, T : Any> ModuleContext.mapBinding(mapName: String) {
    factory(name = mapName, override = true) {
        MultiBindingMap<K, T>(
            emptyMap()
        )
    }
}

infix fun <K : Any, T : Any, S : T> BeanDefinition<S>.intoMap(pair: Pair<String, K>) =
    apply {
        val (mapName, mapKey) = pair

        attributes.getOrSet(KEY_MAP_BINDINGS) { mutableMapOf<String, Any>() }[mapName] = mapKey

        moduleContext.factory(name = mapName, override = true) {
            component.beanRegistry
                .getAllDefinitions()
                .mapNotNull { definition ->
                    definition.attributes.get<Map<String, Any>>(KEY_MAP_BINDINGS)
                        ?.get(mapName)?.let { it to definition }
                }
                .toMap()
                .mapKeys { it.key as K }
                .mapValues { it.value as BeanDefinition<T> }
                .let { MultiBindingMap(it) }
        }
    }

inline fun <K : Any, reified T : Any, reified S : T> ModuleContext.bindIntoMap(
    mapName: String,
    key: K,
    declarationName: String? = null
) =
    factory<T>(UUID.randomUUID().toString()) { get<S>(declarationName) } intoMap (mapName to key)

fun <T : Any> ModuleContext.stringMapBinding(mapName: String) =
    mapBinding<String, T>(mapName)

infix fun <T : Any, S : T> BeanDefinition<S>.intoStringMap(pair: Pair<String, String>) =
    intoMap<String, T, S>(pair)

inline fun <reified T : Any, reified S : T> ModuleContext.bindIntoStringMap(
    mapName: String,
    key: String,
    declarationName: String? = null
) = bindIntoMap<String, T, S>(mapName, key, declarationName)

fun <T : Any> ModuleContext.classMapBinding(mapName: String) =
    mapBinding<KClass<out T>, T>(mapName)

inline infix fun <T : Any, reified S : T> BeanDefinition<S>.intoClassMap(mapName: String) =
    intoMap<KClass<out T>, T, S>(mapName to S::class)

inline fun <reified T : Any, reified S : T> ModuleContext.bindIntoClassMap(
    mapName: String,
    declarationName: String? = null
) = bindIntoMap<KClass<out T>, T, S>(mapName, S::class, declarationName)

fun <T : Any> ModuleContext.intMapBinding(mapName: String) =
    mapBinding<Int, T>(mapName)

infix fun <T : Any, S : T> BeanDefinition<S>.intoIntMap(pair: Pair<String, Int>) =
    intoMap<Int, T, S>(pair)

inline fun <reified T : Any, reified S : T> ModuleContext.bindIntoIntMap(
    mapName: String,
    key: Int,
    declarationName: String? = null
) = bindIntoMap<Int, T, S>(mapName, key, declarationName)

fun <T : Any> ModuleContext.longMapBinding(mapName: String) =
    mapBinding<Long, T>(mapName)

infix fun <T : Any, S : T> BeanDefinition<S>.intoLongMap(pair: Pair<String, Long>) =
    intoMap<Long, T, S>(pair)

inline fun <reified T : Any, reified S : T> ModuleContext.bindIntoLongMap(
    mapName: String,
    key: Long,
    declarationName: String? = null
) = bindIntoMap<Long, T, S>(mapName, key, declarationName)