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

package com.ivianuu.essentials.screenstate

import android.app.KeyguardManager
import android.content.Intent
import android.os.PowerManager
import com.ivianuu.essentials.broadcast.BroadcastFactory
import com.ivianuu.essentials.coroutines.shareIn
import com.ivianuu.essentials.util.AppDispatchers
import com.ivianuu.injekt.Single
import com.ivianuu.injekt.android.ApplicationScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.withContext
import kotlin.time.seconds

/**
 * Provides the current screen state
 */
@ApplicationScope
@Single
class ScreenStateProvider(
    broadcastFactory: BroadcastFactory,
    private val dispatchers: AppDispatchers,
    private val keyguardManager: KeyguardManager,
    private val powerManager: PowerManager
) {

    val screenState = broadcastFactory.create(
        Intent.ACTION_SCREEN_OFF,
        Intent.ACTION_SCREEN_ON,
        Intent.ACTION_USER_PRESENT
    )
        .map { Unit }
        .onStart { emit(Unit) }
        .map { getScreenState() }
        .shareIn(scope = GlobalScope, cacheSize = 1, timeout = 1.seconds, tag = "screen state")


    suspend fun getScreenState() = withContext(dispatchers.default) {
        if (powerManager.isInteractive) {
            if (keyguardManager.isDeviceLocked) {
                ScreenState.Locked
            } else {
                ScreenState.Unlocked
            }
        } else {
            ScreenState.Off
        }
    }

}

enum class ScreenState(val isOn: Boolean) {
    Off(false), Locked(true), Unlocked(true)
}