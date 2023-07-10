/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.unlock

import android.os.PowerManager
import com.ivianuu.essentials.AppContext
import com.ivianuu.essentials.SystemService
import com.ivianuu.essentials.logging.Logger
import com.ivianuu.essentials.logging.log
import com.ivianuu.injekt.Provide
import kotlinx.coroutines.CompletableDeferred
import java.util.UUID
import kotlin.collections.set

fun interface ScreenActivator : suspend () -> Boolean

@Provide fun screenActivator(
  appContext: AppContext,
  logger: Logger,
  powerManager: @SystemService PowerManager
) = ScreenActivator {
  logger.log { "on request is off ? ${!powerManager.isInteractive}" }
  if (powerManager.isInteractive) {
    logger.log { "already on" }
    return@ScreenActivator true
  }

  val result = CompletableDeferred<Boolean>()
  val requestId = UUID.randomUUID().toString()
  requestsById[requestId] = result

  logger.log { "turn screen on $requestId" }

  UnlockActivity.turnScreenOn(appContext, requestId)

  return@ScreenActivator result.await().also {
    logger.log { "screen on result $requestId -> $it" }
  }
}
