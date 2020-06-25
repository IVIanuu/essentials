package com.ivianuu.essentials.store

interface CorruptionHandler {

    suspend fun <T> onCorruption(
        box: Box<T>,
        serialized: String,
        exception: Throwable
    ): T

}

object NoopCorruptionHandler : CorruptionHandler {

    override suspend fun <T> onCorruption(
        box: Box<T>,
        serialized: String,
        exception: Throwable
    ): T {
        throw IllegalStateException(exception)
    }

}

object DefaultDataCorruptionHandler : CorruptionHandler {

    override suspend fun <T> onCorruption(
        box: Box<T>,
        serialized: String,
        exception: Throwable
    ): T = box.defaultData

}
