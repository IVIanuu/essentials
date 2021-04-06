package com.ivianuu.essentials.data

import com.ivianuu.essentials.data.ValueAction.*
import com.ivianuu.essentials.store.Collector
import com.ivianuu.essentials.store.HasResult
import com.ivianuu.essentials.store.emitAndAwait
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.Flow

@Deprecated("Lol", level = DeprecationLevel.WARNING)
interface DataStore<T> : Flow<T> {
    suspend fun update(transform: T.() -> T): T
    fun dispatchUpdate(transform: T.() -> T)
}

sealed class ValueAction<T> {
    data class Update<T>(
        val transform: T.() -> T,
        override val result: CompletableDeferred<T> = CompletableDeferred()
    ) : ValueAction<T>(), HasResult<T>
}

fun <T> Collector<ValueAction<T>>.update(transform: T.() -> T) = emit(Update(transform))

suspend fun <T> Collector<in Update<T>>.updateAndAwait(transform: T.() -> T): T =
    emitAndAwait(Update(transform))
