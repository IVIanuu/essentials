/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.app

import com.ivianuu.essentials.logging.Logger
import com.ivianuu.essentials.logging.log
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.Spread
import com.ivianuu.injekt.common.Eager
import com.ivianuu.injekt.common.TypeKey

fun interface ScopeInitializer<N> : () -> Unit

data class ScopeInitializerElement<N>(
  val key: TypeKey<*>,
  val initializer: ScopeInitializer<*>,
  val loadingOrder: LoadingOrder<out ScopeInitializer<*>>
) {
  companion object {
    @Provide fun <@Spread T : ScopeInitializer<N>, N> scopeInitializerElement(
      initializer: T,
      key: TypeKey<T>,
      loadingOrder: LoadingOrder<T> = LoadingOrder()
    ): ScopeInitializerElement<N> = ScopeInitializerElement(key, initializer, loadingOrder)

    @Provide val descriptor = object : LoadingOrder.Descriptor<ScopeInitializerElement<*>> {
      override fun key(item: ScopeInitializerElement<*>) = item.key

      override fun loadingOrder(item: ScopeInitializerElement<*>) = item.loadingOrder
    }

    @Provide fun <N> defaultElements() = emptyList<ScopeInitializerElement<N>>()
  }
}

class ScopeInitializerRunner<N> @Provide @Eager<N> constructor(
  nameKey: TypeKey<N>,
  initializers: List<ScopeInitializerElement<N>>,
  workerRunner: ScopeWorkerRunner<N>,
  L: Logger
) {
  init {
    initializers
      .sortedWithLoadingOrder()
      .forEach {
        log { "${nameKey.value} initialize ${it.key.value}" }
        it.initializer()
      }
    workerRunner()
  }
}
