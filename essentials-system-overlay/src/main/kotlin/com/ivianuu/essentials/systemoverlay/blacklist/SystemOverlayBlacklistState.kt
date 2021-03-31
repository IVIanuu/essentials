package com.ivianuu.essentials.systemoverlay.blacklist

import com.ivianuu.essentials.recentapps.CurrentApp
import com.ivianuu.essentials.screenstate.ScreenState
import com.ivianuu.essentials.util.Logger
import com.ivianuu.essentials.util.d
import com.ivianuu.injekt.Given
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach

@Given
fun systemOverlayBlacklistState(
    @Given logger: Logger,
    @Given keyboardState: Flow<KeyboardSystemOverlayBlacklistState>,
    @Given lockScreenState: Flow<LockScreenSystemOverlayBlacklistState>,
    @Given secureScreenState: Flow<SecureScreenSystemOverlayBlacklistState>,
    @Given userBlacklistState: Flow<UserBlacklistSystemOverlayBlacklistState>,
): Flow<SystemOverlayBlacklistState> {
    // check the lock screen setting
    return lockScreenState
        .onEach { logger.d { "lock screen state $it" } }
        // check permission screens
        .switchIfStillEnabled { secureScreenState }
        .onEach { logger.d { "secure screen state $it" } }
        // check the user specified blacklist
        .switchIfStillEnabled { userBlacklistState }
        .onEach { logger.d { "user blacklist state $it" } }
        // finally check the keyboard state
        .switchIfStillEnabled { keyboardState }
        .onEach { logger.d { "keyboard state $it" } }
        // distinct
        .distinctUntilChanged()
        .onEach { logger.d { "overlay state changed: $it" } }
        .onCompletion {
            it?.printStackTrace()
            logger.d { "lol $it" }
        }
}

internal typealias LockScreenSystemOverlayBlacklistState = SystemOverlayBlacklistState

@Given
fun lockScreenSystemOverlayEnabledState(
    @Given blacklistPrefs: Flow<SystemOverlayBlacklistPrefs>,
    @Given screenState: Flow<ScreenState>,
): Flow<LockScreenSystemOverlayBlacklistState> = blacklistPrefs
    .map { it.disableOnLockScreen }
    .distinctUntilChanged()
    .flatMapLatest { disableOnLockScreen ->
        if (disableOnLockScreen) {
            screenState
                .map { it != ScreenState.UNLOCKED }
        } else {
            flowOf(false)
        }
    }

internal typealias SecureScreenSystemOverlayBlacklistState = SystemOverlayBlacklistState

@Given
fun secureScreenSystemOverlayBlacklistState(
    @Given blacklistPrefs: Flow<SystemOverlayBlacklistPrefs>,
    @Given isOnSecureScreen: Flow<IsOnSecureScreen>,
    @Given screenState: Flow<ScreenState>
): Flow<SecureScreenSystemOverlayBlacklistState> = blacklistPrefs
    .map { it.disableOnSecureScreens }
    .distinctUntilChanged()
    .flatMapLatest { disableOnSecureScreen ->
        if (disableOnSecureScreen) {
            screenState
                .flatMapLatest { screenState ->
                    if (screenState == ScreenState.UNLOCKED) isOnSecureScreen
                    else flowOf(false)
                }
        } else {
            flowOf(false)
        }
    }

internal typealias UserBlacklistSystemOverlayBlacklistState = SystemOverlayBlacklistState

@Given
fun userBlacklistSystemOverlayBlacklistState(
    @Given blacklistPrefs: Flow<SystemOverlayBlacklistPrefs>,
    @Given currentApp: Flow<CurrentApp>,
    @Given screenState: Flow<ScreenState>,
): Flow<UserBlacklistSystemOverlayBlacklistState> = blacklistPrefs
    .map { it.appBlacklist }
    .distinctUntilChanged()
    .flatMapLatest { blacklist ->
        if (blacklist.isNotEmpty()) {
            screenState
                .flatMapLatest { screenState ->
                    // only check the current app if the screen is on
                    if (screenState == ScreenState.UNLOCKED) {
                        currentApp
                            .map { it in blacklist }
                    } else {
                        flowOf(false)
                    }
                }
        } else {
            flowOf(false)
        }
    }

internal typealias KeyboardSystemOverlayBlacklistState = SystemOverlayBlacklistState

@Given
fun keyboardSystemOverlayBlacklistState(
    @Given blacklistPrefs: Flow<SystemOverlayBlacklistPrefs>,
    @Given keyboardVisible: Flow<KeyboardVisible>
): Flow<KeyboardSystemOverlayBlacklistState> = blacklistPrefs
    .map { it.disableOnKeyboard }
    .distinctUntilChanged()
    .flatMapLatest { disableOnKeyboard ->
        if (disableOnKeyboard) keyboardVisible
        else flowOf(false)
    }

private fun Flow<SystemOverlayBlacklistState>.switchIfStillEnabled(
    next: () -> Flow<SystemOverlayBlacklistState>
): Flow<SystemOverlayBlacklistState> = flatMapLatest {
    if (it) {
        next()
    } else {
        flowOf(it)
    }
}

typealias SystemOverlayBlacklistState = Boolean
