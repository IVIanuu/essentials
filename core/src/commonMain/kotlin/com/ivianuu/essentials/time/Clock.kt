
/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.time

import com.ivianuu.injekt.*
import kotlin.time.*
import kotlin.time.Duration.Companion.nanoseconds

fun interface Clock {
  operator fun invoke(): Duration

  @Provide companion object {
    @Provide val impl = Clock { System.nanoTime().nanoseconds }
  }
}
S