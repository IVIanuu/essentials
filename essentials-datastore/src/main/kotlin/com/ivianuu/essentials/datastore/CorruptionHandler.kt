package com.ivianuu.essentials.datastore

interface CorruptionHandler {

    suspend fun <T> onCorruption(
        dataStore: DataStore<T>,
        serialized: String,
        exception: Throwable
    ): T

}

object NoopCorruptionHandler : CorruptionHandler {

    override suspend fun <T> onCorruption(
        dataStore: DataStore<T>,
        serialized: String,
        exception: Throwable
    ): T {
        throw IllegalStateException(exception)
    }

}

object DefaultDataCorruptionHandler : CorruptionHandler {

    override suspend fun <T> onCorruption(
        dataStore: DataStore<T>,
        serialized: String,
        exception: Throwable
    ): T = dataStore.defaultData

}
