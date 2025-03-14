package essentials.coroutines

import arrow.fx.coroutines.*
import com.github.michaelbull.result.*
import essentials.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.sync.*
import kotlin.coroutines.*

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
    bracketCase(
      acquire = { value },
      use = {
        emit(value)
        awaitCancellation()
      },
      release = { _, _ -> value.onSuccess { release?.invoke(key, it) } }
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
    fa = { block(value) },
    finalizer = { release() }
  )
