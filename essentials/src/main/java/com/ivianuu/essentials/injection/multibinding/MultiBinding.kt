package com.ivianuu.essentials.injection.multibinding

import com.ivianuu.injekt.*
import java.util.*
import kotlin.collections.set

const val KEY_MAP_BINDINGS = "mapBindings"
const val KEY_SET_BINDINGS = "setBindings"

/**
 * Adds a declaration for a map of [K, T] with the name [mapName]
 */
fun <K : Any, T : Any> Module.mapBinding(mapName: String) {
    factory(name = mapName, override = true) {
        MultiBindingMap<K, T>(
            emptyMap()
        )
    }
}

infix fun <K : Any, T : Any, S : T> Declaration<S>.intoMap(pair: Pair<String, K>) =
    apply {
        val (mapName, mapKey) = pair

        attributes.getOrSet(KEY_MAP_BINDINGS) { mutableMapOf<String, Any>() }[mapName] = mapKey

        module.factory(name = mapName, override = true) {
            component.declarationRegistry
                .getAllDeclarations()
                .mapNotNull { declaration ->
                    declaration.attributes.get<Map<String, Any>>(KEY_MAP_BINDINGS)
                        ?.get(mapName)?.let { it to declaration }
                }
                .toMap()
                .mapKeys { it.key as K }
                .mapValues { it.value as Declaration<T> }
                .let { MultiBindingMap(it) }
        }
    }

inline fun <K : Any, reified T : Any, reified S : T> Module.bindIntoMap(
    mapName: String,
    key: K,
    declarationName: String? = null
) =
    factory<T>(UUID.randomUUID().toString()) { get<S>(declarationName) } intoMap (mapName to key)


fun <T : Any> Module.setBinding(setName: String) {
    factory(name = setName, override = true) {
        MultiBindingSet<T>(
            emptySet()
        )
    }
}

infix fun <T : Any, S : T> Declaration<S>.intoSet(setName: String) = apply {
    attributes.getOrSet(KEY_SET_BINDINGS) { mutableSetOf<String>() }.add(setName)

    module.factory(name = setName, override = true) {
        component.declarationRegistry
            .getAllDeclarations()
            .filter { it.attributes.get<Set<String>>(KEY_SET_BINDINGS)?.contains(setName) == true }
            .map { it as Declaration<T> }
            .toSet()
            .let { MultiBindingSet(it) }
    }
}

inline fun <reified T : Any, reified S : T> Module.bindIntoSet(
    setName: String,
    declarationName: String? = null
) =
    factory<T>(UUID.randomUUID().toString()) { get<S>(declarationName) } intoSet setName