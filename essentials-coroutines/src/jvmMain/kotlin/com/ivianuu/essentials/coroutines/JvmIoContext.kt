/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.coroutines

import com.ivianuu.essentials.di.*
import kotlinx.coroutines.*

actual fun ProviderRegistry.ioContext() {
  provide { IOContext(Dispatchers.IO) }
}
