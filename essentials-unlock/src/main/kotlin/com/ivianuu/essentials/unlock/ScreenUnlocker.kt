/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.unlock

import android.app.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.logging.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.android.*
import com.ivianuu.injekt.coroutines.*
import kotlinx.coroutines.*
import java.util.*
import java.util.concurrent.*
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
