/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.screenstate

import android.app.KeyguardManager
import android.content.Intent
import android.os.PowerManager
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import com.ivianuu.essentials.SystemService
import com.ivianuu.essentials.compose.compositionFlow
import com.ivianuu.essentials.util.BroadcastsFactory
import com.ivianuu.injekt.Provide
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

enum class ScreenState(val isOn: Boolean) {
  OFF(false), LOCKED(true), UNLOCKED(true)
}

@Provide fun screenState(
  broadcastsFactory: BroadcastsFactory,
  keyguardManager: @SystemService KeyguardManager,
  powerManager: @SystemService PowerManager
): Flow<ScreenState> = compositionFlow {
  remember {
    broadcastsFactory(
      Intent.ACTION_SCREEN_OFF,
      Intent.ACTION_SCREEN_ON,
      Intent.ACTION_USER_PRESENT
    ).map { it.action }
  }.collectAsState(null).value

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
