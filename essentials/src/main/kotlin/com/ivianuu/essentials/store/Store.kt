package com.ivianuu.essentials.store

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow

interface Store<K, V> {

    suspend fun get(key: K): V?

    suspend fun contains(key: K): Boolean

    suspend fun put(key: K, value: V)

    suspend fun remove(key: K)

    suspend fun clear()

    suspend fun size(): Int // todo use suspend val once supported

    suspend fun keys(): Set<K> // todo use suspend val once supported ?

    fun observeKeyChanges(): Flow<K>

}

suspend fun <K, V> Store<K, V>.putAll(values: Map<K, V>) {
    values.forEach { put(it.key, it.value) }
}

suspend fun <K, V> Store<K, V>.putAll(pairs: Array<Pair<K, V>>) {
    pairs.forEach { put(it.first, it.second) }
}

suspend fun <K, V> Store<K, V>.putAll(pairs: Iterable<Pair<K, V>>) {
    pairs.forEach { put(it.first, it.second) }
}

suspend fun <K, V> Store<K, V>.putAll(pairs: Sequence<Pair<K, V>>) {
    pairs.forEach { put(it.first, it.second) }
}

suspend fun <K, V> Store<K, V>.getAll(): Map<K, V> {
    val map = mutableMapOf<K, V>()
    for (key in keys()) map[key] = get(key)!!
    return map
}

suspend fun <K, V> Store<K, V>.getOrPut(key: K, defaultValue: suspend () -> V): V {
    var value = get(key)
    if (value == null) {
        value = defaultValue()
        put(key, value)
    }

    return value as V
}

suspend fun <K, V> Store<K, V>.getOrPutNullable(key: K, defaultValue: suspend () -> V?): V? {
    return if (contains(key)) {
        get(key)
    } else {
        val value = defaultValue()
        put(key, value as V)
        value
    }
}

suspend fun <K, V> Store<K, V>.getOrDefault(key: K, defaultValue: suspend () -> V): V =
    get(key) ?: defaultValue()

suspend fun <K, V> Store<K, V>.getOrDefaultNullable(key: K, defaultValue: suspend () -> V?): V? =
    if (contains(key)) get(key) else defaultValue()

suspend fun <K, V> Store<K, V>.observeValue(key: K): Flow<V?> {
    return flow {
        emit(get(key)) // send current value

        observeKeyChanges()
            .filter { it == key }
            .collect { emit(get(key)) }
    }
}

suspend fun <K, V> Store<K, V>.observeKeys(): Flow<Set<K>> {
    return flow {
        emit(keys())
        observeKeyChanges().collect { emit(keys()) }
    }
}

suspend fun <K, V> Store<K, V>.observeAllValues(): Flow<Map<K, V>> {
    return flow {
        emit(getAll())
        observeKeyChanges().collect { emit(getAll()) }
    }
}