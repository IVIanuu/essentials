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