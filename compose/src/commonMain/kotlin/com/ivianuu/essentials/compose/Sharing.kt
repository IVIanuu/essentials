@file:OptIn(ExperimentalStdlibApi::class)

package com.ivianuu.essentials.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import com.ivianuu.injekt.Inject
import com.ivianuu.injekt.Provide
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Runnable
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingCommand
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

fun <T> CoroutineScope.sharedComposition(
  sharingStarted: SharingStarted = SharingStarted.WhileSubscribed(0),
  @Inject context: StateCoroutineContext,
  block: @Composable () -> T
): @Composable () -> T {
  val shared = sharedComposition<Unit, T>(sharingStarted) { block() }
  return { shared(Unit) }
}

fun <K, T> CoroutineScope.sharedComposition(
  sharingStarted: SharingStarted = SharingStarted.WhileSubscribed(0),
  @Inject context: StateCoroutineContext,
  block: @Composable (K) -> T
): @Composable (K) -> T {
  val sharedFlows = mutableMapOf<K, SharedFlow<T>>()

  return { key ->
    val sharedFlow = sharedFlows.getOrPut(key) {
      val sharedFlow = MutableSharedFlow<T>(1)

      val delegateDispatcher = (coroutineContext + context)[CoroutineDispatcher]
      val finalDispatcher = object : CoroutineDispatcher() {
        override fun dispatch(context: CoroutineContext, block: Runnable) =
          if (sharedFlow.replayCache.isEmpty() || delegateDispatcher == null)
            block.run()
          else
            delegateDispatcher.dispatch(context, block)
      }

      @Provide val finalContext = context.plus(finalDispatcher)

      launch(finalContext, CoroutineStart.UNDISPATCHED) {
        sharingStarted.command(sharedFlow.subscriptionCount)
          .onStart { emit(SharingCommand.START) }
          .distinctUntilChanged()
          .collectLatest { command ->
            if (command == SharingCommand.START)
              coroutineScope {
                launchComposition(
                  emitter = { sharedFlow.tryEmit(it) },
                  block = { block(key) }
                )
              }
          }
      }

      sharedFlow
    }

    sharedFlow.collectAsState(sharedFlow.replayCache.single()).value
  }
}
