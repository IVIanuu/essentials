/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.app

import com.ivianuu.essentials.di.*
import com.ivianuu.essentials.logging.*

fun interface ScopeInitializer<N> : () -> Unit

inline fun <reified N> ProviderRegistry.scopeInitializers(scopeName: N) {
  provide<ScopeInitializerRunner<N>> { resolve(::ScopeInitializerRunner) }
  eagerInit<N, ScopeInitializerRunner<N>>(scopeName)
}

inline fun <reified N, reified T : ScopeInitializer<N>> ProviderRegistry.scopeInitializer(
  loadingOrder: LoadingOrder<T>,
  crossinline factory: ProviderScope.() -> T
) {
  provideIntoList {
    ScopeInitializerElement<N>(typeKeyOf<T>(), factory(), loadingOrder)
  }
}

data class ScopeInitializerElement<N>(
  val key: TypeKey<*>,
  val initializer: ScopeInitializer<*>,
  val loadingOrder: LoadingOrder<out ScopeInitializer<*>>
)

class ScopeInitializerRunner<N>(
  initializers: List<ScopeInitializerElement<N>>,
  logger: Logger,
  nameKey: TypeKey<N>,
  workerRunner: ScopeWorkerRunner<N>
) {
  init {
    initializers
      .sortedWithLoadingOrder(key = { it.key }, loadingOrder = { it.loadingOrder })
      .forEach {
        logger.log { "$nameKey initialize ${it.key}" }
        it.initializer()
      }
    workerRunner()
  }
}
