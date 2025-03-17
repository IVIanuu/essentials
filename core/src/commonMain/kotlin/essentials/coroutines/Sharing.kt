package essentials.coroutines

import arrow.fx.coroutines.*
import com.github.michaelbull.result.*
import essentials.*
import injekt.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.sync.*
import kotlin.coroutines.*
import kotlin.time.*

fun <K, V> sharedFlows(
  stopTimeout: Duration = Duration.ZERO,
  replayExpiration: Duration = Duration.ZERO,
  replay: Int = 0,
  scope: CoroutineScope = inject,
  create: (K) -> Flow<V>
): (K) -> Flow<V> {
  val statesLock = Mutex()

  class SharedFlowState<V>(
    private val key: K,
    private val states: MutableMap<K, SharedFlowState<V>>
  ) : CoroutineScope {
    override val coroutineContext: CoroutineContext =
      scope.coroutineContext + Job(scope.coroutineContext.job)

    private var wasEverSubscribed = false

    val sharedFlow = create(key).shareIn(
      scope = this,
      started = { subs ->
        subs
          .transformLatest { count ->
            if (count > 0) {
              wasEverSubscribed = true
              emit(SharingCommand.START)
            } else if (wasEverSubscribed) {
              delay(stopTimeout)
              if (replayExpiration > Duration.ZERO) {
                emit(SharingCommand.STOP)
                delay(replayExpiration)
              }
              launch(start = CoroutineStart.UNDISPATCHED) {
                statesLock.withLock { states.remove(key) }
                this@SharedFlowState.coroutineContext.job.cancel()
              }
            }
          }
      },
      replay = replay
    )
  }

  val states = mutableMapOf<K, SharedFlowState<V>>()

  return { key ->
    flow {
      emitAll(
        statesLock.withLock {
          states.getOrPut(key) { SharedFlowState(key, states) }
        }.sharedFlow
      )
    }
  }
}

fun <K, V> sharedAsync(
  timeout: Duration = Duration.ZERO,
  context: CoroutineContext = EmptyCoroutineContext,
  scope: CoroutineScope = inject,
  block: suspend (K) -> V
): suspend (K) -> V {
  val flows = sharedFlows(
    stopTimeout = timeout,
    replay = 1,
    create = { key: K ->
      flow { emit(catch { block(key) }) }
        .flowOn(context)
    }
  )

  return { flows(it).first().getOrThrow() }
}

data class Releasable<T>(val value: T, val release: () -> Unit)

fun <K, T> sharedResource(
  timeout: Duration = Duration.ZERO,
  release: (suspend (K, T) -> Unit)? = null,
  scope: CoroutineScope = inject,
  create: suspend (K) -> T
): suspend (K) -> Releasable<T> {
  val flows: (K) -> Flow<Result<T, Throwable>> = sharedFlows(timeout, replay = 1) { key: K ->
    flow {
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
