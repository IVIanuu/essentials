/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.unlock

import android.app.KeyguardManager
import com.ivianuu.essentials.AppContext
import com.ivianuu.essentials.SystemService
import com.ivianuu.essentials.logging.Logger
import com.ivianuu.essentials.logging.log
import com.ivianuu.injekt.Provide
import kotlinx.coroutines.CompletableDeferred
import java.util.UUID
import kotlin.collections.set

fun interface ScreenUnlocker : suspend () -> Boolean

@Provide fun screenUnlocker(
  context: AppContext,
  keyguardManager: @SystemService KeyguardManager,
  logger: Logger
) = ScreenUnlocker {
  logger.log { "on request is locked ? ${keyguardManager.isKeyguardLocked}" }
  if (!keyguardManager.isKeyguardLocked) {
    logger.log { "already unlocked" }
    return@ScreenUnlocker true
  }

  val result = CompletableDeferred<Boolean>()
  val requestId = UUID.randomUUID().toString()
  requestsById[requestId] = result

  logger.log { "unlock screen $requestId" }

  UnlockActivity.unlockScreen(context, requestId)

  return@ScreenUnlocker result.await().also {
    logger.log { "unlock result $requestId -> $it" }
  }
}
