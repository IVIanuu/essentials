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
import com.ivianuu.essentials.broadcast.broadcasts
import com.ivianuu.essentials.util.DefaultDispatcher
import com.ivianuu.essentials.util.GlobalScope
import com.ivianuu.essentials.util.Logger
import com.ivianuu.injekt.Binding
import com.ivianuu.injekt.FunBinding
import com.ivianuu.injekt.merge.ApplicationComponent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.withContext

typealias ScreenStateFlow = Flow<ScreenState>

@Binding(ApplicationComponent::class)
fun screenStateFlow(
    broadcasts: broadcasts,
    getCurrentScreenState: getCurrentScreenState,
    globalScope: GlobalScope,
    logger: Logger,
): ScreenStateFlow {
    return merge(
        broadcasts(Intent.ACTION_SCREEN_OFF),
        broadcasts(Intent.ACTION_SCREEN_ON),
        broadcasts(Intent.ACTION_USER_PRESENT)
    )
        .onStart { logger.d("sub for screen state") }
        .onCompletion { logger.d("dispose screen state") }
        .map { Unit }
        .onStart { emit(Unit) }
        .map { getCurrentScreenState() }
        .distinctUntilChanged()
        .shareIn(globalScope, 1, SharingStarted.WhileSubscribed())
}

@FunBinding
suspend fun getCurrentScreenState(
    defaultDispatcher: DefaultDispatcher,
    keyguardManager: KeyguardManager,
    powerManager: PowerManager,
): ScreenState =
    withContext(defaultDispatcher) {
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

enum class ScreenState(val isOn: Boolean) {
    Off(false), Locked(true), Unlocked(true)
}
