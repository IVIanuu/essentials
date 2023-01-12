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
import java.util.*
import kotlin.collections.set

fun interface ScreenActivator {
  suspend fun activateScreen(): Boolean
}

context(AppContext, Logger, PowerManager) @Provide fun screenActivator(
  coroutineContext: DefaultContext
) = ScreenActivator {
  withContext(coroutineContext) {
    log { "on request is off ? ${!isInteractive}" }
    if (isInteractive) {
      log { "already on" }
      return@withContext true
    }

    val result = CompletableDeferred<Boolean>()
    val requestId = UUID.randomUUID().toString()
    requestsById[requestId] = result

    log { "turn screen on $requestId" }

    UnlockActivity.turnScreenOn(requestId)

    return@withContext result.await().also {
      log { "screen on result $requestId -> $it" }
    }
  }
}
