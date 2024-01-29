package com.ivianuu.essentials.coroutines

import com.ivianuu.essentials.result.Result
import com.ivianuu.essentials.result.catch
import com.ivianuu.essentials.result.failure
import com.ivianuu.essentials.result.fold
import com.ivianuu.essentials.result.get
import com.ivianuu.essentials.result.getOrThrow
import com.ivianuu.essentials.result.onSuccess
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
  val flows = sharedFlow<K, Result<T, Throwable>>(sharingStarted, 1) { key ->
    emit(catch { block(key) })
  }
  return { flows(it).first().getOrThrow() }
}

data class Releasable<T>(val value: T, val release: () -> Unit)

fun <K, T> CoroutineScope.sharedResource(
  sharingStarted: SharingStarted = SharingStarted.WhileSubscribed(0, 0),
  release: (suspend (K, T) -> Unit)? = null,
  create: suspend (K) -> T
): suspend (K) -> Releasable<T> {
  val flows: (K) -> Flow<Result<T, Throwable>> = sharedFlow(sharingStarted, 1) { key: K ->
    val value = catch { create(key) }
    bracket(
      acquire = { value },
      use = {
        emit(value)
        awaitCancellation()
      },
      release = { _, _ ->
        value.onSuccess { release?.invoke(key, it) }
      }
    )
  }

  return { key ->
    suspendCancellableCoroutine { cont ->
      lateinit var job: Job
      job = launch(cont.context) {
        flows(key).collect { value ->
          value.fold(
            success = { cont.resume(Releasable(it) { job.cancel() }) },
            failure = { cont.resumeWithException(it) }
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
    block = { block(value) },
    finalizer = { release() }
  )
