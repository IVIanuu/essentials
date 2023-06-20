/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.sample

import androidx.compose.runtime.DisposableEffect
import com.ivianuu.essentials.app.ScopeComposition
import com.ivianuu.essentials.app.ScopeWorker
import com.ivianuu.essentials.coroutines.onCancel
import com.ivianuu.essentials.logging.Logger
import com.ivianuu.essentials.logging.log
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.common.TypeKey

@Provide fun <N> scopeLogger(
  scopeKey: TypeKey<N>,
  logger: Logger
) = ScopeWorker<N> {
  logger.log { "${scopeKey.value} created worker" }
  onCancel { logger.log { "${scopeKey.value} disposed worker" } }
}

@Provide fun <N> scopeLogger2(
  scopeKey: TypeKey<N>,
  logger: Logger
) = ScopeComposition<N> {
  DisposableEffect(true) {
    logger.log { "${scopeKey.value} created composition" }
    onDispose { logger.log { "${scopeKey.value} disposed composition" } }
  }
}
