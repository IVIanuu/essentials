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

package com.ivianuu.essentials.screenstate

import android.app.KeyguardManager
import android.content.Intent
import android.os.PowerManager
import com.ivianuu.essentials.broadcast.BroadcastsFactory
import com.ivianuu.essentials.coroutines.DefaultDispatcher
import com.ivianuu.essentials.util.Logger
import com.ivianuu.essentials.util.ScopeCoroutineScope
import com.ivianuu.essentials.util.d
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.scope.AppGivenScope
import com.ivianuu.injekt.scope.Scoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.withContext

@Given
fun screenState(
    @Given broadcastsFactory: BroadcastsFactory,
    @Given logger: Logger,
    @Given scope: ScopeCoroutineScope<AppGivenScope>,
    @Given screenStateProvider: CurrentScreenStateProvider
): @Scoped<AppGivenScope> Flow<ScreenState> = merge(
    broadcastsFactory(Intent.ACTION_SCREEN_OFF),
    broadcastsFactory(Intent.ACTION_SCREEN_ON),
    broadcastsFactory(Intent.ACTION_USER_PRESENT)
)
    .onStart { logger.d { "sub for screen state" } }
    .onCompletion { logger.d { "dispose screen state" } }
    .map { Unit }
    .onStart { emit(Unit) }
    .map { screenStateProvider() }
    .shareIn(scope, SharingStarted.WhileSubscribed(), 1)
    .distinctUntilChanged()

typealias CurrentScreenStateProvider = suspend () -> ScreenState

@Given
fun currentScreenStateProvider(
    @Given defaultDispatcher: DefaultDispatcher,
    @Given keyguardManager: KeyguardManager,
    @Given powerManager: PowerManager,
): CurrentScreenStateProvider = {
    withContext(defaultDispatcher) {
        if (powerManager.isInteractive) {
            if (keyguardManager.isDeviceLocked) {
                ScreenState.LOCKED
            } else {
                ScreenState.UNLOCKED
            }
        } else {
            ScreenState.OFF
        }
    }
}

enum class ScreenState(val isOn: Boolean) {
    OFF(false), LOCKED(true), UNLOCKED(true)
}
