/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.coroutines

import com.ivianuu.injekt.Provide
import kotlinx.coroutines.CoroutineScope

object EsCoroutinesModule {
  @Provide inline fun coroutinesScope(scope: ScopedCoroutineScope<*>): CoroutineScope = scope
}
