package com.ivianuu.essentials.store

import java.util.concurrent.atomic.AtomicReference

internal class CacheBox<T>(private val wrapped: Box<T>) : Box<T> by wrapped {

    private val cachedValue = AtomicReference<Any?>(this)

    override suspend fun set(value: T) {
        cachedValue.set(value)
        wrapped.set(value)
    }

    override suspend fun get(): T {
        var value = cachedValue.get()
        return if (value !== this) {
            value as T
        } else {
            value = wrapped.get()
            cachedValue.set(value)
            value
        }
    }

    override suspend fun delete() {
        cachedValue.set(this)
        wrapped.delete()
    }

}