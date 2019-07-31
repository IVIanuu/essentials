package com.ivianuu.essentials.store

import hu.akarnokd.kotlin.flow.PublishSubject
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap

fun main() {
    val store = MapStore<String, String>()

    GlobalScope.launch {
        store.observeAllValues()
            .collect { println("on change $it") }
    }

    val job = GlobalScope.launch {
        delay(100)
        store.put("one", "1")
        store.put("two", "2")
        store.remove("two")
    }

    while (job.isActive) {
        Thread.sleep(100)
    }
}

class MapStore<K, V>(private val map: ConcurrentHashMap<K, V> = ConcurrentHashMap()) : Store<K, V> {

    private val changes = PublishSubject<K>()

    override suspend fun get(key: K): V? = map[key]

    override suspend fun contains(key: K): Boolean = map.containsKey(key)

    override suspend fun put(key: K, value: V) {
        map[key] = value
        changes.emit(key)
    }

    override suspend fun remove(key: K) {
        map.remove(key)
        changes.emit(key)
    }

    override suspend fun clear() {
        val keys = keys()
        map.clear()
        keys.forEach { changes.emit(it) }
    }

    override suspend fun keys(): Set<K> = map.keys

    override suspend fun size(): Int = map.size

    override fun observeKeyChanges(): Flow<K> = changes

}