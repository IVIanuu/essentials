package essentials.coroutines

import androidx.compose.runtime.*
import app.cash.molecule.*
import injekt.*
import kotlinx.coroutines.*
import kotlin.coroutines.*

@Stable data class CoroutineContexts(
  val main: CoroutineContext = AndroidUiDispatcher.Main,
  val computation: CoroutineContext = Dispatchers.Default,
  val io: CoroutineContext = Dispatchers.IO,
) {
  constructor(coroutineContext: CoroutineContext) : this(coroutineContext, coroutineContext, coroutineContext)

  @Provide companion object {
    @Provide val default = CoroutineContexts()
  }
}
