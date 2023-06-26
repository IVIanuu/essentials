package com.ivianuu.essentials.coroutines

import com.ivianuu.injekt.Provide
import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

data class CoroutineContexts(
  val main: CoroutineContext,
  val computation: CoroutineContext,
  val io: CoroutineContext,
) {
  companion object {
    @Provide val default = CoroutineContexts(
      main = Dispatchers.Main,
      computation = Dispatchers.Default,
      io = Dispatchers.IO
    )
  }
}
