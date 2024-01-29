package com.ivianuu.essentials.coroutines

import com.ivianuu.injekt.Provide
import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

data class CoroutineContexts(
  val main: CoroutineContext = Dispatchers.Main,
  val computation: CoroutineContext = Dispatchers.Default,
  val io: CoroutineContext = Dispatchers.IO,
) {
  constructor(coroutineContext: CoroutineContext) : this(coroutineContext, coroutineContext, coroutineContext)

  @Provide companion object {
    @Provide val default = CoroutineContexts()
  }
}
