package essentials.coroutines

import essentials.*
import injekt.*
import kotlinx.coroutines.*
import kotlin.coroutines.*

data class CoroutineContexts(
  val main: CoroutineContext = Dispatchers.Main,
  val computation: CoroutineContext = Dispatchers.Default,
  val io: CoroutineContext = Dispatchers.IO,
) {
  constructor(coroutineContext: CoroutineContext) : this(coroutineContext, coroutineContext, coroutineContext)

  @Provide companion object {
    @Provide val default:
        @Service<AppScope> CoroutineContexts = CoroutineContexts()
  }
}

fun coroutineContexts(scope: Scope<*> = inject): CoroutineContexts =
  scope.service()
