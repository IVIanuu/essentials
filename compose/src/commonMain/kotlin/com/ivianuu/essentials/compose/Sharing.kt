@file:OptIn(ExperimentalStdlibApi::class, ExperimentalCoroutinesApi::class)

package com.ivianuu.essentials.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import com.ivianuu.injekt.Provide
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Runnable
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingCommand
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

context(StateCoroutineContext)
fun <T> CoroutineScope.sharedComposition(
  sharingStarted: SharingStarted = SharingStarted.WhileSubscribed(0, 0),
  body: @Composable () -> T
): @Composable () -> T {
  val sharedFlow = MutableSharedFlow<T>(1)

  val delegateDispatcher = (coroutineContext + this@StateCoroutineContext)[CoroutineDispatcher]
  val finalDispatcher = object : CoroutineDispatcher() {
    override fun dispatch(context: CoroutineContext, block: Runnable) =
      if (sharedFlow.replayCache.isEmpty() || delegateDispatcher == null)
        block.run()
      else
        delegateDispatcher.dispatch(context, block)
  }

  @Provide val finalContext = this@StateCoroutineContext.plus(finalDispatcher)

  launch(finalContext, CoroutineStart.UNDISPATCHED) {
    sharingStarted.command(sharedFlow.subscriptionCount)
      .onStart { emit(SharingCommand.START) }
      .distinctUntilChanged()
      .collectLatest { command ->
        if (command == SharingCommand.START)
          coroutineScope {
            launchComposition(
              emitter = { sharedFlow.tryEmit(it) },
              body = body
            )
          }
      }
  }

  return { sharedFlow.collectAsState(sharedFlow.replayCache.single()).value }
}
