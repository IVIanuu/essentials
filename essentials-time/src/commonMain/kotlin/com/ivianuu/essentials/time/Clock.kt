/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.time

import com.ivianuu.essentials.di.*
import kotlin.time.*

fun interface Clock : () -> Duration

internal expect val clock: Clock

fun ProviderRegistry.time() {
  provide { clock }
}
