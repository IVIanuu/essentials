/*
 * Copyright 2020 Manuel Wrage
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ivianuu.essentials.unlock

import android.app.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.coroutines.*
import com.ivianuu.essentials.logging.*
import com.ivianuu.essentials.util.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.android.*
import com.ivianuu.injekt.coroutines.*
import kotlinx.coroutines.*
import java.util.*
import java.util.concurrent.*

typealias ScreenUnlocker = suspend () -> Boolean

@Given fun screenUnlocker(
  @Given appContext: AppContext,
  @Given dispatcher: DefaultDispatcher,
  @Given _: Logger,
  @Given keyguardManager: @SystemService KeyguardManager,
): ScreenUnlocker = {
  withContext(dispatcher) {
    if (!keyguardManager.isKeyguardLocked) return@withContext true

    val result = CompletableDeferred<Boolean>()
    val requestId = UUID.randomUUID().toString()
    requestsById[requestId] = result

    d { "unlock screen $requestId" }

    UnlockScreenActivity.unlock(appContext, requestId)

    return@withContext result.await().also {
      d { "unlock result $requestId -> $it" }
    }
  }
}

private val requestsById = ConcurrentHashMap<String, CompletableDeferred<Boolean>>()

internal fun onUnlockScreenResult(requestId: String, success: Boolean) {
  requestsById.remove(requestId)?.complete(success)
}
