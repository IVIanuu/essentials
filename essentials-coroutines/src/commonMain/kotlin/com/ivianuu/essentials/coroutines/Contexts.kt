/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.coroutines

import com.ivianuu.essentials.di.*
import kotlinx.coroutines.*
import kotlin.coroutines.*

@JvmInline value class DefaultContext(val value: CoroutineContext) {
  companion object {
    inline val context: DefaultContext
      get() = DefaultContext(Dispatchers.Default)
  }
}

@JvmInline value class MainContext(val value: CoroutineContext) {
  companion object {
    inline val context: MainContext
      get() = MainContext(Dispatchers.Main)
  }
}

@JvmInline value class IOContext(val value: CoroutineContext)

fun ProviderRegistry.coroutineContexts() {
  provide { DefaultContext(Dispatchers.Default) }
  provide { MainContext(Dispatchers.Main) }
  ioContext()
}

expect fun ProviderRegistry.ioContext()
