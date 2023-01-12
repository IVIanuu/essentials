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
import java.util.*
import kotlin.collections.set

fun interface ScreenUnlocker {
  suspend fun unlockScreen(): Boolean
}

context(AppContext, KeyguardManager, Logger) @Provide fun screenUnlocker(
  coroutineContext: DefaultContext
) = ScreenUnlocker {
  withContext(coroutineContext) {
    log { "on request is locked ? $isKeyguardLocked" }
    if (!isKeyguardLocked) {
      log { "already unlocked" }
      return@withContext true
    }

    val result = CompletableDeferred<Boolean>()
    val requestId = UUID.randomUUID().toString()
    requestsById[requestId] = result

    log { "unlock screen $requestId" }

    UnlockActivity.unlockScreen(requestId)

    return@withContext result.await().also {
      log { "unlock result $requestId -> $it" }
    }
  }
}
