/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.app

import com.ivianuu.essentials.logging.Logger
import com.ivianuu.essentials.logging.log
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.common.Eager
import com.ivianuu.injekt.common.TypeKey

interface ScopeInitializer<N> : () -> Unit, Service<ScopeInitializer<N>>

class ScopeInitializerRunner<N> @Provide @Eager<N> constructor(
  nameKey: TypeKey<N>,
  initializers: List<ServiceElement<ScopeInitializer<N>>>,
  workerRunner: ScopeWorkerRunner<N>,
  L: Logger
) {
  init {
    initializers
      .sortedWithLoadingOrder()
      .forEach {
        log { "${nameKey.value} initialize ${it.key.value}" }
        it.instance()
      }
    workerRunner()
  }
}
