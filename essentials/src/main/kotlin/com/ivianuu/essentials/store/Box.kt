package com.ivianuu.essentials.store

import kotlinx.coroutines.flow.Flow

interface Box<T> {

    suspend fun set(value: T)

    suspend fun get(): T

    suspend fun delete()

    suspend fun exists(): Boolean

    fun asFlow(): Flow<T>

}

suspend fun <T> Box<T>.getIfExists(): T? = if (exists()) get() else null

suspend inline fun <T> Box<T>.getOrDefault(defaultValue: () -> T): T =
    getIfExists() ?: defaultValue()

suspend inline fun <T> Box<T>.getOrSet(defaultValue: () -> T): T {
    return if (exists()) {
        get()
    } else {
        val value = defaultValue()
        set(value)
        value
    }
}