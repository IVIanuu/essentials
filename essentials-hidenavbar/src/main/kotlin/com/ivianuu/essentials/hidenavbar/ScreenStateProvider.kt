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

package com.ivianuu.essentials.hidenavbar

import android.content.Intent
import android.os.PowerManager
import com.ivianuu.essentials.messaging.BroadcastFactory
import com.ivianuu.injekt.Factory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

/**
 * Provides the current screen state
 */
@Factory
internal class ScreenStateProvider(
    private val broadcastFactory: BroadcastFactory,
    private val powerManager: PowerManager
) {
    val isScreenOn: Boolean get() = powerManager.isInteractive
    val isScreenOff: Boolean get() = !isScreenOn

    fun observeScreenState(): Flow<Boolean> {
        return broadcastFactory.create(
            Intent.ACTION_SCREEN_OFF,
            Intent.ACTION_SCREEN_ON,
            Intent.ACTION_USER_PRESENT
        )
            .map { isScreenOn }
            .onStart { emit(isScreenOn) }
    }
}
