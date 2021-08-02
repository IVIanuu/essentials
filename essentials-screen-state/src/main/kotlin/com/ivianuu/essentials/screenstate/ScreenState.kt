/*
 * Copyright 2021 Manuel Wrage
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

import android.app.*
import android.content.*
import android.os.*
import com.ivianuu.essentials.broadcast.*
import com.ivianuu.essentials.logging.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.android.*
import com.ivianuu.injekt.coroutines.*
import com.ivianuu.injekt.scope.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

enum class ScreenState(val isOn: Boolean) {
  OFF(false), LOCKED(true), UNLOCKED(true)
}

@Provide fun screenState(
  broadcastsFactory: BroadcastsFactory,
  logger: Logger,
  scope: InjektCoroutineScope<AppScope>,
  screenStateProvider: CurrentScreenStateProvider
): @Scoped<AppScope> Flow<ScreenState> = merge(
  broadcastsFactory(Intent.ACTION_SCREEN_OFF),
  broadcastsFactory(Intent.ACTION_SCREEN_ON),
  broadcastsFactory(Intent.ACTION_USER_PRESENT)
)
  .onStart { d { "sub for screen state" } }
  .onCompletion { d { "dispose screen state" } }
  .map { Unit }
  .onStart { emit(Unit) }
  .map { screenStateProvider() }
  .shareIn(scope, SharingStarted.WhileSubscribed(), 1)
  .distinctUntilChanged()

private typealias CurrentScreenStateProvider = suspend () -> ScreenState

@Provide fun currentScreenStateProvider(
  dispatcher: DefaultDispatcher,
  keyguardManager: @SystemService KeyguardManager,
  powerManager: @SystemService PowerManager,
): CurrentScreenStateProvider = {
  withContext(dispatcher) {
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
