package com.ivianuu.essentials.coroutines

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingCommand
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.coroutines.resume

fun <T> CoroutineScope.sharedFlow(
  sharingStarted: SharingStarted = SharingStarted.WhileSubscribed(0, 0),
  replay: Int = 0,
  block: suspend FlowCollector<T>.() -> Unit
): SharedFlow<T> = flow { block() }.shareIn(this, sharingStarted, replay)

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
            sharedFlow<T>(
              { subs ->
                sharingStarted.command(subs)
                  .onEach {
                    if (it == SharingCommand.STOP_AND_RESET_REPLAY_CACHE)
                      mutex.withLock { map.remove(key) }
                  }
              },
              replay
            ) { block(this, key) }
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
  val flows = sharedFlow<K, T>(sharingStarted, 1) { key -> emit(block(key)) }
  return { flows(it).first() }
}

fun <T> CoroutineScope.sharedComputation(
  sharingStarted: SharingStarted = SharingStarted.WhileSubscribed(0, 0),
  block: suspend () -> T
): suspend () -> T {
  val keyed = sharedComputation<Unit, T>(sharingStarted) { block() }
  return { keyed(Unit) }
}

data class Releasable<T>(val value: T, val release: () -> Unit)

fun <T> CoroutineScope.sharedResource(
  sharingStarted: SharingStarted = SharingStarted.WhileSubscribed(0, 0),
  release: (suspend (T) -> Unit)? = null,
  create: suspend () -> T
): suspend () -> Releasable<T> {
  val keyed = sharedResource<Unit, T>(
    sharingStarted,
    release?.let { { _, value -> release(value) } }
  ) { create() }
  return { keyed(Unit) }
}

fun <K, T> CoroutineScope.sharedResource(
  sharingStarted: SharingStarted = SharingStarted.WhileSubscribed(0, 0),
  release: (suspend (K, T) -> Unit)? = null,
  create: suspend (K) -> T
): suspend (K) -> Releasable<T> {
  val flows = sharedFlow(sharingStarted, 1) { key: K ->
    val value = create(key)
    bracket(
      acquire = { value },
      use = {
        emit(value)
        awaitCancellation()
      },
      release = { _, _ -> release?.invoke(key, value) }
    )
  }

  return { key ->
    suspendCancellableCoroutine { cont ->
      lateinit var job: Job
      job = launch(cont.context) {
        flows(key).collect { value ->
          cont.resume(Releasable(value) { job.cancel() })
          awaitCancellation()
        }
      }

      cont.invokeOnCancellation { job.cancel() }
    }
  }
}

suspend fun <K, T, R> (suspend (K) -> Releasable<T>).use(key: K, block: suspend (T) -> R): R =
  invoke(key).use(block)

suspend fun <T, R> (suspend () -> Releasable<T>).use(block: suspend (T) -> R): R =
  invoke().use(block)

suspend fun <T, R> Releasable<T>.use(block: suspend (T) -> R): R =
  guarantee(
    block = { block(value) },
    finalizer = { release() }
  )
