package com.ivianuu.essentials.coroutines

import app.cash.quiver.extensions.orThrow
import arrow.core.Either
import arrow.fx.coroutines.bracketCase
import arrow.fx.coroutines.guarantee
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

fun <K, T> CoroutineScope.sharedFlow(
  sharingStarted: SharingStarted = SharingStarted.WhileSubscribed(0, 0),
  replay: Int = 0,
  block: suspend FlowCollector<T>.(K) -> Unit
): (K) -> Flow<T> {
  val map = mutableMapOf<K, SharedFlow<T>>()
  val mutex = Mutex()
  return { key ->
    flow {
      emitAll(
        mutex.withLock {
          map.getOrPut(key) {
            flow { block(this, key) }.shareIn(this@sharedFlow, sharingStarted, replay)
          }
        }
      )
    }
  }
}

fun <K, T> CoroutineScope.sharedComputation(
  sharingStarted: SharingStarted = SharingStarted.WhileSubscribed(0, 0),
  block: suspend (K) -> T
): suspend (K) -> T {
  val flows = sharedFlow<K, Either<Throwable, T>>(sharingStarted, 1) { key ->
    emit(Either.catch { block(key) })
  }
  return { flows(it).first().orThrow() }
}

data class Releasable<T>(val value: T, val release: () -> Unit)

fun <K, T> CoroutineScope.sharedResource(
  sharingStarted: SharingStarted = SharingStarted.WhileSubscribed(0, 0),
  release: (suspend (K, T) -> Unit)? = null,
  create: suspend (K) -> T
): suspend (K) -> Releasable<T> {
  val flows: (K) -> Flow<Either<Throwable, T>> = sharedFlow(sharingStarted, 1) { key: K ->
    val value = Either.catch { create(key) }
    bracketCase(
      acquire = { value },
      use = {
        emit(value)
        awaitCancellation()
      },
      release = { _, _ ->
        value.onRight { release?.invoke(key, it) }
      }
    )
  }

  return { key ->
    suspendCancellableCoroutine { cont ->
      lateinit var job: Job
      job = launch(cont.context) {
        flows(key).collect { value ->
          value.fold(
            ifLeft = { cont.resumeWithException(it) },
            ifRight = { cont.resume(Releasable(it) { job.cancel() }) }
          )
          awaitCancellation()
        }
      }

      cont.invokeOnCancellation { job.cancel() }
    }
  }
}

suspend fun <K, T, R> (suspend (K) -> Releasable<T>).use(key: K, block: suspend (T) -> R): R =
  invoke(key).use(block)

suspend fun <T, R> Releasable<T>.use(block: suspend (T) -> R): R =
  guarantee(
    fa = { block(value) },
    finalizer = { release() }
  )
