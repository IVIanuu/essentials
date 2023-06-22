/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
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
  logger: Logger,
  mainSwitchState: Flow<SystemOverlayEnabled>,
  keyboardState: @Private Flow<@Keyboard SystemOverlayBlacklistState>,
  lockScreenState: @Private Flow<@LockScreen SystemOverlayBlacklistState>,
  secureScreenState: @Private Flow<@SecureScreen SystemOverlayBlacklistState>,
  userBlacklistState: @Private Flow<@UserBlacklist SystemOverlayBlacklistState>
): Flow<SystemOverlayBlacklistState> = mainSwitchState
  // check the commonMain state of the overlay
  .map {
    if (it.value) SystemOverlayBlacklistState.ENABLED
    else SystemOverlayBlacklistState.DISABLED
  }
  .distinctUntilChanged()
  .onEach { logger.log { "system overlay enabled $it" } }
  // after that check the lock screen setting
  .switchIfStillEnabled { lockScreenState }
  .onEach { logger.log { "lock screen state $it" } }
  // check permission screens
  .switchIfStillEnabled { secureScreenState }
  .onEach { logger.log { "secure screen state $it" } }
  // check the user specified blacklist
  .switchIfStillEnabled { userBlacklistState }
  .onEach { logger.log { "user blacklist state $it" } }
  // finally check the keyboard state
  .switchIfStillEnabled { keyboardState }
  .onEach { logger.log { "keyboard state $it" } }
  // distinct
  .distinctUntilChanged()
  .onEach { logger.log { "overlay state changed: $it" } }
  .onCompletion {
    it?.printStackTrace()
    logger.log { "lol $it" }
  }

@Tag private annotation class LockScreen

@Provide fun lockScreenState(
  logger: Logger,
  pref: DataStore<SystemOverlayBlacklistPrefs>,
  screenState: Flow<ScreenState>,
): @Private Flow<@LockScreen SystemOverlayBlacklistState> = pref.data
  .map { it.disableOnLockScreen }
  .distinctUntilChanged()
  .flatMapLatest { disableOnLockScreen ->
    if (disableOnLockScreen) {
      screenState
        .map {
          logger.log { "screen state $it disable on lock $disableOnLockScreen" }
          if (it != ScreenState.UNLOCKED) {
            logger.log { "hide: on lock screen" }
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
  logger: Logger,
  pref: DataStore<SystemOverlayBlacklistPrefs>,
  isOnSecureScreen: Flow<IsOnSecureScreen>,
  screenState: Flow<ScreenState>,
): @Private Flow<@SecureScreen SystemOverlayBlacklistState> = pref.data
  .map { it.disableOnSecureScreens }
  .distinctUntilChanged()
  .onEach { logger.log { "disable on secure screens $it" } }
  .flatMapLatest { disableOnSecureScreen ->
    if (disableOnSecureScreen) {
      screenState
        .onEach { logger.log { "screen state $it" } }
        .flatMapLatest { screenState ->
          if (screenState == ScreenState.UNLOCKED) {
            isOnSecureScreen
              .onEach { logger.log { "is on secure screen $it" } }
              .map {
                if (it.value) {
                  logger.log { "hide: secure screen" }
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
  logger: Logger,
  pref: DataStore<SystemOverlayBlacklistPrefs>,
  currentApp: Flow<CurrentApp?>,
  screenState: Flow<ScreenState>,
): @Private Flow<@UserBlacklist SystemOverlayBlacklistState> = pref.data
  .map { it.appBlacklist }
  .distinctUntilChanged()
  .onEach { logger.log { "blacklist $it" } }
  .flatMapLatest { blacklist ->
    if (blacklist.isNotEmpty()) {
      screenState
        .onEach { logger.log { "screen state $it" } }
        .flatMapLatest { screenState ->
          // only check the current app if the screen is on
          if (screenState == ScreenState.UNLOCKED) {
            currentApp
              .onEach { logger.log { "current app $it" } }
              .map { currentApp ->
                if (currentApp?.value in blacklist) {
                  logger.log { "hide: user blacklist" }
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
  logger: Logger,
  pref: DataStore<SystemOverlayBlacklistPrefs>,
  keyboardVisible: Flow<KeyboardVisible>
): @Private Flow<@Keyboard SystemOverlayBlacklistState> = pref.data
  .map { it.disableOnKeyboard }
  .distinctUntilChanged()
  .flatMapLatest { disableOnKeyboard ->
    if (disableOnKeyboard) {
      keyboardVisible
        .map {
          if (it.value) {
            logger.log { "hide: keyboard" }
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
