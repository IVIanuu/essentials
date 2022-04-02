/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.unlock

import android.os.PowerManager
import com.ivianuu.essentials.AppContext
import com.ivianuu.essentials.logging.Logger
import com.ivianuu.essentials.logging.log
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.android.SystemService
import com.ivianuu.injekt.coroutines.DefaultContext
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.withContext
import java.util.concurrent.ConcurrentHashMap
import kotlin.collections.set

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
