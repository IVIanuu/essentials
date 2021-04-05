package com.ivianuu.essentials.data

import com.ivianuu.essentials.data.StoreAction.*
import com.ivianuu.essentials.store.Collector
import com.ivianuu.essentials.store.emitAndAwait
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.Flow

@Deprecated("Lol", level = DeprecationLevel.WARNING)
interface DataStore<T> : Flow<T> {
    suspend fun update(transform: T.() -> T): T
    fun dispatchUpdate(transform: T.() -> T)
}

sealed class StoreAction<T> {
    data class Update<T>(val transform: T.() -> T) : StoreAction<T>(),
        CompletableDeferred<T> by CompletableDeferred()
}

fun <T> Collector<StoreAction<T>>.tryUpdate(transform: T.() -> T) = tryEmit(Update(transform))

suspend fun <T> Collector<in Update<T>>.update(transform: T.() -> T): T =
    emitAndAwait(Update(transform))
