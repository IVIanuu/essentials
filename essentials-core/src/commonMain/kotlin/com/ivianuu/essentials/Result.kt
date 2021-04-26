package com.ivianuu.essentials

import com.github.michaelbull.result.*
import kotlin.contracts.*
import kotlin.coroutines.cancellation.*

inline fun <V> V.ok() = Ok(this)

inline fun <E> E.err() = Err(this)

fun <V> Result<V, *>.getOrNull(): V? = getOrElse { null }

inline fun <V> catch(@BuilderInference block: () -> V): Result<V, Throwable> = try {
    Ok(block())
} catch (e: CancellationException) {
    throw e
} catch (e: Throwable) {
    Err(e)
}

inline fun <V, E> result(@BuilderInference block: ResultBinding<E>.() -> V): Result<V, E> {
    val receiver = ResultBindingImpl<E>()
    return try {
        with(receiver) { Ok(block()) }
    } catch (e: ResultBindingImpl.ExitException) {
        receiver.error
    }
}

interface ResultBinding<A> {
    fun <T> Result<T, A>.bind(): T
}

@PublishedApi
internal class ResultBindingImpl<E> : ResultBinding<E> {
    lateinit var error: Err<E>
    override fun <T> Result<T, E>.bind(): T = when (this) {
        is Ok -> value
        is Err -> {
            this@ResultBindingImpl.error = this
            throw ExitException
        }
    }

    object ExitException : Exception()
}
