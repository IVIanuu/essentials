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

import com.ivianuu.essentials.logging.*
import com.ivianuu.essentials.recentapps.*
import com.ivianuu.essentials.screenstate.*
import com.ivianuu.essentials.systemoverlay.*
import com.ivianuu.injekt.*
import kotlinx.coroutines.flow.*

enum class SystemOverlayBlacklistState { DISABLED, ENABLED, HIDDEN }

typealias SystemOverlayEnabled = Boolean

@Provide fun systemOverlayBlacklistState(
  mainSwitchState: Flow<SystemOverlayEnabled>,
  keyboardState: @Private Flow<KeyboardSystemOverlayBlacklistState>,
  lockScreenState: @Private Flow<LockScreenSystemOverlayBlacklistState>,
  secureScreenState: @Private Flow<SecureScreenSystemOverlayBlacklistState>,
  userBlacklistState: @Private Flow<UserBlacklistSystemOverlayBlacklistState>,
  logger: Logger
): Flow<SystemOverlayBlacklistState> = mainSwitchState
  // check the main state of the overlay
  .map {
    if (it) SystemOverlayBlacklistState.ENABLED
    else SystemOverlayBlacklistState.DISABLED
  }
  .distinctUntilChanged()
  .onEach { d { "system overlay enabled $it" } }
  // after that check the lock screen setting
  .switchIfStillEnabled { lockScreenState }
  .onEach { d { "lock screen state $it" } }
  // check permission screens
  .switchIfStillEnabled { secureScreenState }
  .onEach { d { "secure screen state $it" } }
  // check the user specified blacklist
  .switchIfStillEnabled { userBlacklistState }
  .onEach { d { "user blacklist state $it" } }
  // finally check the keyboard state
  .switchIfStillEnabled { keyboardState }
  .onEach { d { "keyboard state $it" } }
  // distinct
  .distinctUntilChanged()
  .onEach { d { "overlay state changed: $it" } }
  .onCompletion {
    it?.printStackTrace()
    d { "lol $it" }
  }

private typealias LockScreenSystemOverlayBlacklistState = SystemOverlayBlacklistState

@Provide fun lockScreenSystemOverlayEnabledState(
  blacklistPrefs: Flow<SystemOverlayBlacklistPrefs>,
  logger: Logger,
  screenState: Flow<ScreenState>,
): @Private Flow<LockScreenSystemOverlayBlacklistState> = blacklistPrefs
  .map { it.disableOnLockScreen }
  .distinctUntilChanged()
  .flatMapLatest { disableOnLockScreen ->
    if (disableOnLockScreen) {
      screenState
        .map {
          d { "screen state $it disable on lock $disableOnLockScreen" }
          if (it != ScreenState.UNLOCKED) {
            d { "hide: on lock screen" }
            SystemOverlayBlacklistState.HIDDEN
          } else {
            SystemOverlayBlacklistState.ENABLED
          }
        }
    } else {
      flowOf(SystemOverlayBlacklistState.ENABLED)
    }
  }

private typealias SecureScreenSystemOverlayBlacklistState = SystemOverlayBlacklistState

@Provide fun secureScreenSystemOverlayBlacklistState(
  blacklistPrefs: Flow<SystemOverlayBlacklistPrefs>,
  isOnSecureScreen: Flow<IsOnSecureScreen>,
  logger: Logger,
  screenState: Flow<ScreenState>
): @Private Flow<SecureScreenSystemOverlayBlacklistState> = blacklistPrefs
  .map { it.disableOnSecureScreens }
  .distinctUntilChanged()
  .onEach { d { "disable on secure screens $it" } }
  .flatMapLatest { disableOnSecureScreen ->
    if (disableOnSecureScreen) {
      screenState
        .onEach { d { "screen state $it" } }
        .flatMapLatest { screenState ->
          if (screenState == ScreenState.UNLOCKED) {
            isOnSecureScreen
              .onEach { d { "is on secure screen $it" } }
              .map {
                if (it) {
                  d { "hide: secure screen" }
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

private typealias UserBlacklistSystemOverlayBlacklistState = SystemOverlayBlacklistState

@Provide fun userBlacklistSystemOverlayBlacklistState(
  blacklistPrefs: Flow<SystemOverlayBlacklistPrefs>,
  currentApp: Flow<CurrentApp>,
  logger: Logger,
  screenState: Flow<ScreenState>,
): @Private Flow<UserBlacklistSystemOverlayBlacklistState> = blacklistPrefs
  .map { it.appBlacklist }
  .distinctUntilChanged()
  .onEach { d { "blacklist $it" } }
  .flatMapLatest { blacklist ->
    if (blacklist.isNotEmpty()) {
      screenState
        .onEach { d { "screen state $it" } }
        .flatMapLatest { screenState ->
          // only check the current app if the screen is on
          if (screenState == ScreenState.UNLOCKED) {
            currentApp
              .onEach { d { "current app $it" } }
              .map { currentApp ->
                if (currentApp in blacklist) {
                  d { "hide: user blacklist" }
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

private typealias KeyboardSystemOverlayBlacklistState = SystemOverlayBlacklistState

@Provide fun keyboardSystemOverlayBlacklistState(
  blacklistPrefs: Flow<SystemOverlayBlacklistPrefs>,
  logger: Logger,
  keyboardVisible: Flow<KeyboardVisible>,
): @Private Flow<KeyboardSystemOverlayBlacklistState> = blacklistPrefs
  .map { it.disableOnKeyboard }
  .distinctUntilChanged()
  .flatMapLatest { disableOnKeyboard ->
    if (disableOnKeyboard) {
      keyboardVisible
        .map {
          if (it) {
            d { "hide: keyboard" }
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

