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
import com.ivianuu.injekt.coroutines.DefaultContext
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.withContext
import java.util.concurrent.ConcurrentHashMap
import kotlin.collections.set

fun interface ScreenUnlocker : suspend () -> Boolean

@Provide fun screenUnlocker(
  context: AppContext,
  coroutineContext: DefaultContext,
  L: Logger,
  keyguardManager: @SystemService KeyguardManager
) = ScreenUnlocker {
  withContext(coroutineContext) {
    log { "on request is locked ? ${keyguardManager.isKeyguardLocked}" }
    if (!keyguardManager.isKeyguardLocked) {
      log { "already unlocked" }
      return@withContext true
    }

    val result = CompletableDeferred<Boolean>()
    val requestId = UUID.randomUUID().toString()
    requestsById[requestId] = result

    log { "unlock screen $requestId" }

    UnlockScreenActivity.unlock(context, requestId)

    return@withContext result.await().also {
      log { "unlock result $requestId -> $it" }
    }
  }
}

private val requestsById = ConcurrentHashMap<String, CompletableDeferred<Boolean>>()

internal fun onUnlockScreenResult(requestId: String, success: Boolean) {
  requestsById.remove(requestId)?.complete(success)
}
