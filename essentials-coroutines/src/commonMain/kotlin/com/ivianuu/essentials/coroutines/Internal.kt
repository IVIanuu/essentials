/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.coroutines

@PublishedApi internal expect inline fun <T> synchronized(lock: Any, block: () -> T): T
