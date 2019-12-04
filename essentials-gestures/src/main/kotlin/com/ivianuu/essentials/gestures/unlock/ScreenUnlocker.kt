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

package com.ivianuu.essentials.gestures.unlock

import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import com.ivianuu.essentials.messaging.BroadcastFactory
import com.ivianuu.injekt.Factory
import hu.akarnokd.kotlin.flow.takeUntil
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.singleOrNull
import kotlinx.coroutines.flow.take

/**
 * Helper class for unlocking the screen
 */
@Factory
class ScreenUnlocker(
    private val broadcastFactory: BroadcastFactory,
    private val context: Context,
    private val keyguardManager: KeyguardManager
) {

    suspend fun unlockScreen(): Boolean {
        if (keyguardManager.isKeyguardLocked) {
            context.startActivity(
                Intent(context, UnlockScreenActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            )

            return broadcastFactory.create(ACTION_ON_UNLOCK_RESULT)
                .map { it.getBooleanExtra(EXTRA_SUCCESS, false) }
                .takeUntil(flowOf(Unit).onStart { delay(5000) })
                .take(1)
                .singleOrNull()
                ?: false
        }

        return true
    }

    fun screenUnlockResult(success: Boolean) {
        context.sendBroadcast(Intent(ACTION_ON_UNLOCK_RESULT).apply {
            putExtra(EXTRA_SUCCESS, success)
        })
    }

    private companion object {
        private const val ACTION_ON_UNLOCK_RESULT =
            "com.ivianuu.essentials.gestures.ON_UNLOCK_RESULT"
        private const val EXTRA_SUCCESS = "success"
    }
}
