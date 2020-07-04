/*
 * Copyright 2019 Manuel Wrage
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

import android.app.KeyguardManager
import android.content.Context
import com.ivianuu.essentials.util.AppCoroutineDispatchers
import com.ivianuu.essentials.util.Logger
import com.ivianuu.injekt.ApplicationComponent
import com.ivianuu.injekt.ForApplication
import com.ivianuu.injekt.Scoped
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.withContext
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * Helper class for unlocking the screen
 */
@Scoped(ApplicationComponent::class)
class UnlockScreen(
    private val context: @ForApplication Context,
    private val dispatchers: AppCoroutineDispatchers,
    private val logger: Logger,
    private val keyguardManager: KeyguardManager
) {

    private val requestsById = ConcurrentHashMap<String, CompletableDeferred<Boolean>>()

    suspend operator fun invoke(): Boolean = withContext(dispatchers.default) {
        if (!keyguardManager.isKeyguardLocked) return@withContext true

        val result = CompletableDeferred<Boolean>()
        val requestId = UUID.randomUUID().toString()
        requestsById[requestId] = result

        logger.d("unlock screen $requestId")

        UnlockScreenActivity.unlock(context, requestId)

        return@withContext result.await().also {
            logger.d("unlock result $requestId -> $it")
        }
    }

    internal fun onUnlockScreenResult(requestId: String, success: Boolean) {
        requestsById.remove(requestId)?.complete(success)
    }
}
