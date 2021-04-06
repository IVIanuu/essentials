package com.ivianuu.essentials.data

import com.ivianuu.essentials.data.ValueAction.*
import com.ivianuu.essentials.store.Sink
import com.ivianuu.essentials.store.ResultAction
import com.ivianuu.essentials.store.sendAndAwait

sealed class ValueAction<T> {
    data class Update<T>(val transform: T.() -> T) : ValueAction<T>(), ResultAction<T> by ResultAction()
}

fun <T> Sink<ValueAction<T>>.update(transform: T.() -> T) = send(Update(transform))

suspend fun <T> Sink<Update<T>>.updateAndAwait(transform: T.() -> T): T =
    sendAndAwait(Update(transform))
