/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.serialization

import com.ivianuu.essentials.*
import com.ivianuu.essentials.di.*
import kotlinx.serialization.json.*

fun ProviderRegistry.serialization() {
  provide { scoped(AppScope) { Json { ignoreUnknownKeys = true } } }
}
