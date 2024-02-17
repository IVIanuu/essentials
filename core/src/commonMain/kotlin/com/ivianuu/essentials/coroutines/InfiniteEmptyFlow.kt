/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.coroutines

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

private val InfiniteEmptyFlow = flow<Nothing> { awaitCancellation() }
fun <T> infiniteEmptyFlow(): Flow<T> = InfiniteEmptyFlow
