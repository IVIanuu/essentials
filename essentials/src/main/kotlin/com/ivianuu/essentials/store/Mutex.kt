package com.ivianuu.essentials.store

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred

internal class MutexBox<T>(private val wrapped: Box<T>) : Box<T> by wrapped {

    private var currentDeferred: Deferred<T>? = null
    private val deferredLock = Any()

    override suspend fun get(): T {
        var deferred = synchronized(deferredLock) { currentDeferred }
        if (deferred == null) {
            deferred = CompletableDeferred()
            synchronized(deferredLock) { currentDeferred = deferred }
            try {
                val result = wrapped.get()
                deferred.complete(result)
            } catch (e: Exception) {
                deferred.completeExceptionally(e)
            } finally {
                @Suppress("DeferredResultUnused")
                synchronized(deferredLock) { currentDeferred = null }
            }
        }

        return deferred.await()
    }

}