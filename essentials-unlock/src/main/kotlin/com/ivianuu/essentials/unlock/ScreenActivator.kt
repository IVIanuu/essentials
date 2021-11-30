/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.unlock

import android.os.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.logging.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.android.*
import com.ivianuu.injekt.coroutines.*
import kotlinx.coroutines.*
import java.util.*
import java.util.concurrent.*
import kotlin.collections.set
import kotlin.coroutines.*

fun interface ScreenActivator : suspend () -> Boolean

@Provide fun screenActivator(
  context: AppContext,
  coroutineContext: DefaultContext,
  L: Logger,
  powerManager: @SystemService PowerManager
) = ScreenActivator {
  withContext(coroutineContext) {
    log { "on request is off ? ${!powerManager.isInteractive}" }
    if (powerManager.isInteractive){
      log { "already on" }
      return@withContext true
    }

    val result = CompletableDeferred<Boolean>()
    val requestId = UUID.randomUUID().toString()
    requestsById[requestId] = result

    log { "turn screen on $requestId" }

    ScreenOnActivity.turnOn(context, requestId)

    return@withContext result.await().also {
      log { "screen on result $requestId -> $it" }
    }
  }
}

private val requestsById = ConcurrentHashMap<String, CompletableDeferred<Boolean>>()

internal fun onScreenOnResult(requestId: String, success: Boolean) {
  requestsById.remove(requestId)?.complete(success)
}
