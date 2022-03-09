/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.coroutines

import com.ivianuu.essentials.di.*
import kotlinx.coroutines.*
import kotlin.coroutines.*

fun ProviderRegistry.namedCoroutineScope() {
  provideGeneric { key ->
    if (key.classifierFqName != classifierFqNameOf<NamedCoroutineScope<*>>()) null
    else {
      val scopeNameKey = key.arguments[0]
      Provider {
        scoped(scopeNameKey, key as TypeKey<Any>) {
          val context = get(typeKeyOf<NamedCoroutineContext<*>>().copy(arguments = arrayOf(scopeNameKey)))
          NamedCoroutineScope<Any?>(
            object : CoroutineScope {
              override val coroutineContext: CoroutineContext = context.value + SupervisorJob()
            }
          )
        }
      }
    }
  }

  provideDefaultGeneric { key ->
    if (key.classifierFqName != classifierFqNameOf<NamedCoroutineContext<*>>()) null
    else {
      Provider { NamedCoroutineContext<Any?>(Dispatchers.Default) }
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

data class NamedCoroutineContext<N>(val value: CoroutineContext)
