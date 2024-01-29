
/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.time

import com.ivianuu.injekt.Provide
import kotlin.time.Duration

fun interface Clock {
  operator fun invoke(): Duration
}

@Provide expect val DefaultClock: Clock
