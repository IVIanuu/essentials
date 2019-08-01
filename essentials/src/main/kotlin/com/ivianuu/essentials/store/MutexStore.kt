package com.ivianuu.essentials.store

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred

class MutexStore<K, V>(private val wrapped: Store<K, V>) : Store<K, V> by wrapped {

    private val deferredByKey = hashMapOf<K, Deferred<V?>>()

    override suspend fun get(key: K): V? {
        var deferred = synchronized(deferredByKey) { deferredByKey[key] }
        if (deferred == null) {
            deferred = CompletableDeferred()
            synchronized(deferredByKey) { deferredByKey[key] = deferred }
            try {
                val result = wrapped.get(key)
                deferred.complete(result)
            } catch (e: Exception) {
                deferred.completeExceptionally(e)
            } finally {
                @Suppress("DeferredResultUnused")
                synchronized(deferredByKey) { deferredByKey.remove(key) }
            }
        }

        return deferred.await()
    }
}