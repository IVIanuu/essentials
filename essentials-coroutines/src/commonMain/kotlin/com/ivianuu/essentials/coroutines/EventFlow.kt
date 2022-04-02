/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.coroutines

import kotlinx.coroutines.flow.MutableSharedFlow

fun <T> EventFlow(): MutableSharedFlow<T> = MutableSharedFlow(
  extraBufferCapacity = Int.MAX_VALUE
)
