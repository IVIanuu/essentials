/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.app

import com.ivianuu.essentials.logging.Logger
import com.ivianuu.essentials.logging.log
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.common.ProvidedElement
import com.ivianuu.injekt.common.TypeKey

interface ScopeInitializer<N> : () -> Unit, Service<ScopeInitializer<N>>

@Provide inline fun <N> scopeInitializerRunner(
  crossinline key: () -> TypeKey<ScopeInitializerRunner>,
  crossinline nameKey: () -> TypeKey<N>,
  crossinline initializers: () -> List<ServiceElement<ScopeInitializer<N>>>,
  crossinline workerRunner: () -> ScopeWorkerRunner<N>,
  crossinline logger: () -> Logger
) = object : ProvidedElement<N, ScopeInitializerRunner> {
  override val key: TypeKey<ScopeInitializerRunner>
    get() = key()

  override fun init() {
    @Provide val logger = logger()
    initializers()
      .sortedWithLoadingOrder()
      .forEach {
        log { "${nameKey().value} initialize ${it.key.value}" }
        it.instance()
      }
    workerRunner()()
  }

  override fun get(): ScopeInitializerRunner = ScopeInitializerRunner
}

object ScopeInitializerRunner
