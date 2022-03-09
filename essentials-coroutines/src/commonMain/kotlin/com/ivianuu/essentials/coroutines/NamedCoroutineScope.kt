/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.coroutines

import com.ivianuu.essentials.di.*
import kotlinx.coroutines.*
import kotlin.coroutines.*

inline fun <reified N> ProviderRegistry.namedCoroutineScope(scopeName: N) {
  provide {
    scoped(scopeName) {
      val context = get<NamedCoroutineContext<N>>()
      NamedCoroutineScope<N>(
        object : CoroutineScope {
          override val coroutineContext: CoroutineContext = context.value + SupervisorJob()
        }
      )
    }
  }
}

class NamedCoroutineScope<N>(val value: CoroutineScope) : CoroutineScope, Disposable {
  override val coroutineContext: CoroutineContext
    get() = value.coroutineContext

  override fun dispose() {
    value.cancel()
  }
}

@JvmInline value class NamedCoroutineContext<N>(val value: CoroutineContext)
