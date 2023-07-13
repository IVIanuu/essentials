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

context(Logger) @Provide fun <N> scopeLogger(
  scopeKey: TypeKey<N>
) = ScopeWorker<N> {
  log { "${scopeKey.value} created worker" }
  onCancel { log { "${scopeKey.value} disposed worker" } }
}

context(Logger) @Provide fun <N> scopeLogger2(
  scopeKey: TypeKey<N>
) = ScopeComposition<N> {
  DisposableEffect(true) {
    log { "${scopeKey.value} created composition" }
    onDispose { log { "${scopeKey.value} disposed composition" } }
  }
}
