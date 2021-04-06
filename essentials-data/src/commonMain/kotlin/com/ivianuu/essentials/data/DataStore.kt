package com.ivianuu.essentials.data

import com.ivianuu.essentials.data.ValueAction.*
import com.ivianuu.essentials.store.Sink
import com.ivianuu.essentials.store.HasResult
import com.ivianuu.essentials.store.sendAndAwait
import kotlinx.coroutines.CompletableDeferred

sealed class ValueAction<T> {
    data class Update<T>(
        val transform: T.() -> T,
        override val result: CompletableDeferred<T> = CompletableDeferred()
    ) : ValueAction<T>(), HasResult<T>
}

fun <T> Sink<ValueAction<T>>.update(transform: T.() -> T) = send(Update(transform))

suspend fun <T> Sink<in Update<T>>.updateAndAwait(transform: T.() -> T): T =
    sendAndAwait(Update(transform))
