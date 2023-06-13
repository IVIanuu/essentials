/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.coroutines

@PublishedApi internal actual inline fun <T> synchronized(lock: Any, block: () -> T): T =
  kotlin.synchronized(lock, block)
