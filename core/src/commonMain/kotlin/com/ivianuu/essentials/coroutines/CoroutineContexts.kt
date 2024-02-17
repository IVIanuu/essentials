package com.ivianuu.essentials.coroutines

import com.ivianuu.injekt.*
import kotlinx.coroutines.*
import kotlin.coroutines.*

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
