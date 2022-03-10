/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.state

import com.ivianuu.injekt.*
import kotlin.coroutines.*

actual object StateContextInjectables {
  @Provide actual inline val context: StateContext
    get() = EmptyCoroutineContext
}
