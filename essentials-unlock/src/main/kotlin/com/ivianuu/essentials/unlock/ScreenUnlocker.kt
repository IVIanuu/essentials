/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.unlock

import android.app.KeyguardManager
import com.ivianuu.essentials.AppContext
import com.ivianuu.essentials.logging.Logger
import com.ivianuu.essentials.logging.log
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.android.SystemService
import com.ivianuu.injekt.common.DefaultCoroutineContext
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.collections.set

fun interface ScreenUnlocker : suspend () -> Boolean

@Provide fun screenUnlocker(
  context: AppContext,
  coroutineContext: DefaultCoroutineContext,
  keyguardManager: @SystemService KeyguardManager,
  logger: Logger
) = ScreenUnlocker {
  withContext(coroutineContext) {
    logger.log { "on request is locked ? ${keyguardManager.isKeyguardLocked}" }
    if (!keyguardManager.isKeyguardLocked) {
      logger.log { "already unlocked" }
      return@withContext true
    }

    val result = CompletableDeferred<Boolean>()
    val requestId = UUID.randomUUID().toString()
    requestsById[requestId] = result

    logger.log { "unlock screen $requestId" }

    UnlockActivity.unlockScreen(context, requestId)

    return@withContext result.await().also {
      logger.log { "unlock result $requestId -> $it" }
    }
  }
}

