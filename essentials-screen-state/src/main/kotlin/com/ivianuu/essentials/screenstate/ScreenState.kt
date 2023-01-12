/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.screenstate

import android.app.KeyguardManager
import android.content.Intent
import android.os.PowerManager
import com.ivianuu.essentials.util.BroadcastsFactory
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.Tag
import com.ivianuu.injekt.android.SystemService
import com.ivianuu.injekt.coroutines.DefaultContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.withContext

enum class ScreenState(val isOn: Boolean) {
  OFF(false), LOCKED(true), UNLOCKED(true);

  @JvmInline value class Provider(val screenState: Flow<ScreenState>)
}

context(BroadcastsFactory, CurrentScreenStateProvider)
    @Provide fun screenStateProvider() = ScreenState.Provider(
  broadcasts(
    Intent.ACTION_SCREEN_OFF,
    Intent.ACTION_SCREEN_ON,
    Intent.ACTION_USER_PRESENT
  )
    .onStart<Any?> { emit(Unit) }
    .map { currentScreenState() }
    .distinctUntilChanged()
)

fun interface CurrentScreenStateProvider {
  suspend fun currentScreenState(): ScreenState
}

@Provide fun currentScreenStateProvider(
  context: DefaultContext,
  keyguardManager: @SystemService KeyguardManager,
  powerManager: @SystemService PowerManager,
) = CurrentScreenStateProvider {
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
