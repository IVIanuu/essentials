package com.ivianuu.essentials.store

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap
import kotlin.coroutines.coroutineContext

class MutexStore<K, V>(private val wrapped: Store<K, V>) : Store<K, V> by wrapped {

    private val deferredByKey = hashMapOf<K, Deferred<V?>>()

    override suspend fun get(key: K): V? {
        val deferred = synchronized(this) {
            deferredByKey.getOrPut(key) {
                val deferred = CompletableDeferred<V?>()

                // launch a new coroutine to break out
                GlobalScope.launch(coroutineContext) {
                    try {
                        val result = wrapped.get(key)
                        deferred.complete(result)
                    } catch (e: Exception) {
                        deferred.completeExceptionally(e)
                    } finally {
                        synchronized(this@MutexStore) {
                            deferredByKey.remove(key)
                        }
                    }
                }

                return@getOrPut deferred
            }
        }

        return deferred.await()
    }
}