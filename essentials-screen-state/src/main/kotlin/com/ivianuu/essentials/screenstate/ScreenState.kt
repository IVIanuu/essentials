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
import com.ivianuu.essentials.util.d
import com.ivianuu.essentials.util.dispatchers
import com.ivianuu.injekt.Reader
import com.ivianuu.injekt.given
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.withContext

@Reader
val screenState: Flow<ScreenState>
    get() = BroadcastFactory.create(
        Intent.ACTION_SCREEN_OFF,
        Intent.ACTION_SCREEN_ON,
        Intent.ACTION_USER_PRESENT
    )
        .onStart { d { "sub for screen state" } }
        .onCompletion { d { "dispose screen state" } }
        .map { Unit }
        .onStart { emit(Unit) }
        .map { getCurrentScreenState() }
        .distinctUntilChanged()

@Reader
private suspend fun getCurrentScreenState(): ScreenState =
    withContext(dispatchers.default) {
        if (given<PowerManager>().isInteractive) {
            if (given<KeyguardManager>().isDeviceLocked) {
                ScreenState.Locked
            } else {
                ScreenState.Unlocked
            }
        } else {
            ScreenState.Off
        }
    }

enum class ScreenState(val isOn: Boolean) {
    Off(false), Locked(true), Unlocked(true)
}
