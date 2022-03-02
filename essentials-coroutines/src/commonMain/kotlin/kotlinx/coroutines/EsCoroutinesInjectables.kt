/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.coroutines

import com.ivianuu.injekt.*
import com.ivianuu.injekt.coroutines.*

object EsCoroutinesInjectables {
  @Provide inline fun coroutinesScope(scope: NamedCoroutineScope<*>): CoroutineScope = scope
}
