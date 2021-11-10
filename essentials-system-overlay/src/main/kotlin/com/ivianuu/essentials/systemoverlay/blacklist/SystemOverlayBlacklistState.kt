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

package com.ivianuu.essentials.systemoverlay.blacklist

import com.ivianuu.essentials.data.DataStore
import com.ivianuu.essentials.logging.Logger
import com.ivianuu.essentials.logging.log
import com.ivianuu.essentials.recentapps.CurrentApp
import com.ivianuu.essentials.screenstate.ScreenState
import com.ivianuu.essentials.systemoverlay.IsOnSecureScreen
import com.ivianuu.essentials.systemoverlay.KeyboardVisible
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.Tag
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach

enum class SystemOverlayBlacklistState { DISABLED, ENABLED, HIDDEN }

@JvmInline value class SystemOverlayEnabled(val value: Boolean)

@Provide fun systemOverlayBlacklistState(
  mainSwitchState: Flow<SystemOverlayEnabled>,
  keyboardState: @Private Flow<@Keyboard SystemOverlayBlacklistState>,
  lockScreenState: @Private Flow<@LockScreen SystemOverlayBlacklistState>,
  secureScreenState: @Private Flow<@SecureScreen SystemOverlayBlacklistState>,
  userBlacklistState: @Private Flow<@UserBlacklist SystemOverlayBlacklistState>,
  L: Logger
): Flow<SystemOverlayBlacklistState> = mainSwitchState
  // check the main state of the overlay
  .map {
    if (it.value) SystemOverlayBlacklistState.ENABLED
    else SystemOverlayBlacklistState.DISABLED
  }
  .distinctUntilChanged()
  .onEach { log { "system overlay enabled $it" } }
  // after that check the lock screen setting
  .switchIfStillEnabled { lockScreenState }
  .onEach { log { "lock screen state $it" } }
  // check permission screens
  .switchIfStillEnabled { secureScreenState }
  .onEach { log { "secure screen state $it" } }
  // check the user specified blacklist
  .switchIfStillEnabled { userBlacklistState }
  .onEach { log { "user blacklist state $it" } }
  // finally check the keyboard state
  .switchIfStillEnabled { keyboardState }
  .onEach { log { "keyboard state $it" } }
  // distinct
  .distinctUntilChanged()
  .onEach { log { "overlay state changed: $it" } }
  .onCompletion {
    it?.printStackTrace()
    log { "lol $it" }
  }

@Tag private annotation class LockScreen

@Provide fun lockScreenState(
  pref: DataStore<SystemOverlayBlacklistPrefs>,
  screenState: Flow<ScreenState>,
  L: Logger
): @Private Flow<@LockScreen SystemOverlayBlacklistState> = pref.data
  .map { it.disableOnLockScreen }
  .distinctUntilChanged()
  .flatMapLatest { disableOnLockScreen ->
    if (disableOnLockScreen) {
      screenState
        .map {
          log { "screen state $it disable on lock $disableOnLockScreen" }
          if (it != ScreenState.UNLOCKED) {
            log { "hide: on lock screen" }
            SystemOverlayBlacklistState.HIDDEN
          } else {
            SystemOverlayBlacklistState.ENABLED
          }
        }
    } else {
      flowOf(SystemOverlayBlacklistState.ENABLED)
    }
  }

@Tag private annotation class SecureScreen

@Provide fun secureScreenState(
  pref: DataStore<SystemOverlayBlacklistPrefs>,
  isOnSecureScreen: Flow<IsOnSecureScreen>,
  screenState: Flow<ScreenState>,
  L: Logger
): @Private Flow<@SecureScreen SystemOverlayBlacklistState> = pref.data
  .map { it.disableOnSecureScreens }
  .distinctUntilChanged()
  .onEach { log { "disable on secure screens $it" } }
  .flatMapLatest { disableOnSecureScreen ->
    if (disableOnSecureScreen) {
      screenState
        .onEach { log { "screen state $it" } }
        .flatMapLatest { screenState ->
          if (screenState == ScreenState.UNLOCKED) {
            isOnSecureScreen
              .onEach { log { "is on secure screen $it" } }
              .map {
                if (it.value) {
                  log { "hide: secure screen" }
                  SystemOverlayBlacklistState.HIDDEN
                } else {
                  SystemOverlayBlacklistState.ENABLED
                }
              }
          } else {
            flowOf(SystemOverlayBlacklistState.ENABLED)
          }
        }
    } else {
      flowOf(SystemOverlayBlacklistState.ENABLED)
    }
  }

@Tag private annotation class UserBlacklist

@Provide fun userBlacklistState(
  pref: DataStore<SystemOverlayBlacklistPrefs>,
  currentApp: Flow<CurrentApp?>,
  screenState: Flow<ScreenState>,
  L: Logger
): @Private Flow<@UserBlacklist SystemOverlayBlacklistState> = pref.data
  .map { it.appBlacklist }
  .distinctUntilChanged()
  .onEach { log { "blacklist $it" } }
  .flatMapLatest { blacklist ->
    if (blacklist.isNotEmpty()) {
      screenState
        .onEach { log { "screen state $it" } }
        .flatMapLatest { screenState ->
          // only check the current app if the screen is on
          if (screenState == ScreenState.UNLOCKED) {
            currentApp
              .onEach { log { "current app $it" } }
              .map { currentApp ->
                if (currentApp?.value in blacklist) {
                  log { "hide: user blacklist" }
                  SystemOverlayBlacklistState.HIDDEN
                } else {
                  SystemOverlayBlacklistState.ENABLED
                }
              }
          } else {
            flowOf(SystemOverlayBlacklistState.ENABLED)
          }
        }
    } else {
      flowOf(SystemOverlayBlacklistState.ENABLED)
    }
  }

@Tag private annotation class Keyboard

@Provide fun keyboardState(
  pref: DataStore<SystemOverlayBlacklistPrefs>,
  keyboardVisible: Flow<KeyboardVisible>,
  L: Logger
): @Private Flow<@Keyboard SystemOverlayBlacklistState> = pref.data
  .map { it.disableOnKeyboard }
  .distinctUntilChanged()
  .flatMapLatest { disableOnKeyboard ->
    if (disableOnKeyboard) {
      keyboardVisible
        .map {
          if (it.value) {
            log { "hide: keyboard" }
            SystemOverlayBlacklistState.HIDDEN
          } else {
            SystemOverlayBlacklistState.ENABLED
          }
        }
    } else {
      flowOf(SystemOverlayBlacklistState.ENABLED)
    }
  }

private fun Flow<SystemOverlayBlacklistState>.switchIfStillEnabled(
  next: () -> Flow<SystemOverlayBlacklistState>
): Flow<SystemOverlayBlacklistState> = flatMapLatest {
  if (it == SystemOverlayBlacklistState.ENABLED) {
    next()
  } else {
    flowOf(it)
  }
}

@Tag private annotation class Private
