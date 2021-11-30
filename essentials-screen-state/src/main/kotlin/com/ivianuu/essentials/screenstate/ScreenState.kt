/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.screenstate

import android.app.*
import android.content.*
import android.os.*
import com.ivianuu.essentials.util.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.android.*
import com.ivianuu.injekt.coroutines.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

enum class ScreenState(val isOn: Boolean) {
  OFF(false), LOCKED(true), UNLOCKED(true)
}

@Provide fun screenState(
  broadcastsFactory: BroadcastsFactory,
  screenStateProvider: @CurrentScreenStateProvider suspend () -> ScreenState
): Flow<ScreenState> = broadcastsFactory(
  Intent.ACTION_SCREEN_OFF,
  Intent.ACTION_SCREEN_ON,
  Intent.ACTION_USER_PRESENT
)
  .onStart<Any?> { emit(Unit) }
  .map { screenStateProvider() }
  .distinctUntilChanged()

@Tag private annotation class CurrentScreenStateProvider

@Provide fun currentScreenStateProvider(
  context: DefaultContext,
  keyguardManager: @SystemService KeyguardManager,
  powerManager: @SystemService PowerManager,
): @CurrentScreenStateProvider suspend () -> ScreenState = {
  withContext(context) {
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
