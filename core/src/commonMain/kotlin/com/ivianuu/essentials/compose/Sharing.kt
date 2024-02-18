@file:OptIn(ExperimentalStdlibApi::class)

package com.ivianuu.essentials.compose

import androidx.compose.runtime.*
import com.ivianuu.injekt.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.coroutines.*

fun <K, T> CoroutineScope.sharedComposition(
  sharingStarted: SharingStarted = SharingStarted.WhileSubscribed(0),
  context: CoroutineContext = EmptyCoroutineContext,
  @Inject stateContext: StateCoroutineContext,
  block: @Composable (K) -> T
): @Composable (K) -> T {
  val sharedFlows = mutableMapOf<K, SharedFlow<T>>()

  return { key ->
    val sharedFlow = sharedFlows.getOrPut(key) {
      val sharedFlow = MutableSharedFlow<T>(1)

      val tmpContext = coroutineContext + stateContext + context

      val delegateDispatcher = tmpContext[CoroutineDispatcher]
      val finalDispatcher = object : CoroutineDispatcher() {
        override fun dispatch(context: CoroutineContext, block: Runnable) =
          if (sharedFlow.replayCache.isEmpty() || delegateDispatcher == null)
            block.run()
          else
            delegateDispatcher.dispatch(context, block)
      }

      @Provide val finalContext = tmpContext + finalDispatcher

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

    sharedFlow.collect(sharedFlow.replayCache.single())
  }
}
