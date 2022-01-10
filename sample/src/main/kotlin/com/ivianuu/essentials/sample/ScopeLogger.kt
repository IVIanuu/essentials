/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.sample

import com.ivianuu.essentials.app.*
import com.ivianuu.essentials.coroutines.*
import com.ivianuu.essentials.logging.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.common.*

@Provide fun <N> scopeLogger(
  componentKey: TypeKey<N>,
  L: Logger
) = ScopeWorker<N> {
  log { "${componentKey.value} created" }
  onCancel { log { "${componentKey.value} disposed" } }
}
