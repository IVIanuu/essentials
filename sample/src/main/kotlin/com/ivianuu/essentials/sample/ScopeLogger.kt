/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.sample

import com.ivianuu.essentials.app.ScopeWorker
import com.ivianuu.essentials.coroutines.onCancel
import com.ivianuu.essentials.logging.Logger
import com.ivianuu.essentials.logging.log
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.common.TypeKey

context(Logger) @Provide fun <N> scopeLogger(
  componentKey: TypeKey<N>
) = ScopeWorker<N> {
  log { "${componentKey.value} created" }
  onCancel { log { "${componentKey.value} disposed" } }
}
