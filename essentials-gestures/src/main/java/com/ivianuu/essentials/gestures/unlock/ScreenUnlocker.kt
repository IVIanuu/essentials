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
import com.ivianuu.injekt.Inject
import com.ivianuu.kommon.core.content.newTask
import com.ivianuu.kommon.core.content.startActivity
import io.reactivex.Observable
import kotlinx.coroutines.rx2.awaitFirstOrDefault
import java.util.concurrent.TimeUnit

/**
 * Helper class for unlocking the screen
 */
@Inject
class ScreenUnlocker(
    private val broadcastFactory: BroadcastFactory,
    private val context: Context,
    private val keyguardManager: KeyguardManager
) {

    suspend fun unlockScreen(): Boolean {
        if (keyguardManager.isKeyguardLocked) {
            context.startActivity<UnlockScreenActivity> { newTask() }

            return broadcastFactory.create(ACTION_ON_UNLOCK_RESULT)
                .map { it.getBooleanExtra(EXTRA_SUCCESS, false) }
                .takeUntil(Observable.just(Unit).delay(5, TimeUnit.SECONDS))
                .take(1)
                .awaitFirstOrDefault(true)
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