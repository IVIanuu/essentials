/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.coroutines

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.produceIn
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn

context(CoroutineScope) inline fun <T> Flow<T>.launch() = launchIn(this@CoroutineScope)

context(CoroutineScope) inline fun <T> Flow<T>.share(
  started: SharingStarted,
  replay: Int = 0
) = shareIn(this@CoroutineScope, started, replay)

context(CoroutineScope) inline fun <T> Flow<T>.state(
  started: SharingStarted,
  initialValue: T
) = stateIn(this@CoroutineScope, started, initialValue)

context(CoroutineScope) inline fun <T> Flow<T>.produce() = produceIn(this@CoroutineScope)

