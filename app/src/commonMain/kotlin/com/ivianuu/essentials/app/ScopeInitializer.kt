/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.app

import com.ivianuu.essentials.ProvidedService
import com.ivianuu.essentials.Scope
import com.ivianuu.essentials.ScopeObserver
import com.ivianuu.essentials.cast
import com.ivianuu.essentials.logging.Logger
import com.ivianuu.essentials.logging.log
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.common.TypeKey

interface ScopeInitializer<N> : () -> Unit, ExtensionPoint<ScopeInitializer<N>>

@Provide inline fun <N> scopeInitializerRunner(
  crossinline key: () -> TypeKey<ScopeInitializerRunner>,
  crossinline nameKey: () -> TypeKey<N>,
  crossinline initializers: (Scope<N>) -> List<ExtensionPointRecord<ScopeInitializer<N>>>,
  crossinline workerRunner: (Scope<N>) -> ScopeWorkerRunner<N>,
  crossinline compositionRunner: (Scope<N>) -> ScopeCompositionRunner<N>,
  crossinline logger: (Scope<N>) -> Logger
): ProvidedService<N, ScopeInitializerRunner> = object : ProvidedService<N, ScopeInitializerRunner>, ScopeObserver {
  override val key: TypeKey<ScopeInitializerRunner>
    get() = key()

  override fun onEnter(scope: Scope<*>) {
    initializers(scope.cast())
      .sortedWithLoadingOrder()
      .forEach {
        logger(scope.cast()).log { "${nameKey().value} initialize ${it.key.value}" }
        it.instance()
      }
    workerRunner(scope.cast())()
    compositionRunner(scope.cast())()
  }

  override fun get(scope: Scope<N>) = ScopeInitializerRunner
}

object ScopeInitializerRunner
