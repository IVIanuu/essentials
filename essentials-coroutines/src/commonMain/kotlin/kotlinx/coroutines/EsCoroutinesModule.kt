/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.coroutines

import com.ivianuu.essentials.coroutines.ScopedCoroutineScope
import com.ivianuu.injekt.Provide

object EsCoroutinesModule {
  @Provide inline fun coroutinesScope(scope: ScopedCoroutineScope<*>): CoroutineScope = scope
}
