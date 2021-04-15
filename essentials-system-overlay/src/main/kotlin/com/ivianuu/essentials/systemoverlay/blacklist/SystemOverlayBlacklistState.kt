package com.ivianuu.essentials.systemoverlay.blacklist

import com.ivianuu.essentials.recentapps.CurrentApp
import com.ivianuu.essentials.screenstate.ScreenState
import com.ivianuu.essentials.systemoverlay.IsOnSecureScreen
import com.ivianuu.essentials.systemoverlay.KeyboardVisible
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
    @Given mainSwitchState: Flow<SystemOverlayEnabled>,
    @Given lockScreenState: Flow<LockScreenSystemOverlayBlacklistState>,
    @Given secureScreenState: Flow<SecureScreenSystemOverlayBlacklistState>,
    @Given userBlacklistState: Flow<UserBlacklistSystemOverlayBlacklistState>,
): Flow<SystemOverlayBlacklistState> = mainSwitchState
    // check the main state of the overlay
    .map {
        if (it) SystemOverlayBlacklistState.ENABLED
        else SystemOverlayBlacklistState.DISABLED
    }
    .distinctUntilChanged()
    .onEach { logger.d { "system overlay enabled $it" } }
    // after that check the lock screen setting
    .switchIfStillEnabled { lockScreenState }
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

typealias SystemOverlayEnabled = Boolean

internal typealias LockScreenSystemOverlayBlacklistState = SystemOverlayBlacklistState

@Given
fun lockScreenSystemOverlayEnabledState(
    @Given blacklistPrefs: Flow<SystemOverlayBlacklistPrefs>,
    @Given logger: Logger,
    @Given screenState: Flow<ScreenState>,
): Flow<LockScreenSystemOverlayBlacklistState> = blacklistPrefs
    .map { it.disableOnLockScreen }
    .distinctUntilChanged()
    .flatMapLatest { disableOnLockScreen ->
        if (disableOnLockScreen) {
            screenState
                .map {
                    logger.d { "screen state $it disable on lock $disableOnLockScreen" }
                    if (it != ScreenState.UNLOCKED) {
                        logger.d { "hide: on lock screen" }
                        SystemOverlayBlacklistState.HIDDEN
                    } else {
                        SystemOverlayBlacklistState.ENABLED
                    }
                }
        } else {
            flowOf(SystemOverlayBlacklistState.ENABLED)
        }
    }

internal typealias SecureScreenSystemOverlayBlacklistState = SystemOverlayBlacklistState

@Given
fun secureScreenSystemOverlayBlacklistState(
    @Given blacklistPrefs: Flow<SystemOverlayBlacklistPrefs>,
    @Given isOnSecureScreen: Flow<IsOnSecureScreen>,
    @Given logger: Logger,
    @Given screenState: Flow<ScreenState>
): Flow<SecureScreenSystemOverlayBlacklistState> = blacklistPrefs
    .map { it.disableOnSecureScreens }
    .distinctUntilChanged()
    .onEach { logger.d { "disable on secure screens $it" } }
    .flatMapLatest { disableOnSecureScreen ->
        if (disableOnSecureScreen) {
            screenState
                .onEach { logger.d { "screen state $it" } }
                .flatMapLatest { screenState ->
                    if (screenState == ScreenState.UNLOCKED) {
                        isOnSecureScreen
                            .onEach { logger.d { "is on secure screen $it" } }
                            .map {
                                if (it) {
                                    logger.d { "hide: secure screen" }
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

internal typealias UserBlacklistSystemOverlayBlacklistState = SystemOverlayBlacklistState

@Given
fun userBlacklistSystemOverlayBlacklistState(
    @Given blacklistPrefs: Flow<SystemOverlayBlacklistPrefs>,
    @Given currentApp: Flow<CurrentApp>,
    @Given logger: Logger,
    @Given screenState: Flow<ScreenState>,
): Flow<UserBlacklistSystemOverlayBlacklistState> = blacklistPrefs
    .map { it.appBlacklist }
    .distinctUntilChanged()
    .onEach { logger.d { "blacklist $it" } }
    .flatMapLatest { blacklist ->
        if (blacklist.isNotEmpty()) {
            screenState
                .onEach { logger.d { "screen state $it" } }
                .flatMapLatest { screenState ->
                    // only check the current app if the screen is on
                    if (screenState == ScreenState.UNLOCKED) {
                        currentApp
                            .onEach { logger.d { "current app $it" } }
                            .map { currentApp ->
                                if (currentApp in blacklist) {
                                    logger.d { "hide: user blacklist" }
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

internal typealias KeyboardSystemOverlayBlacklistState = SystemOverlayBlacklistState

@Given
fun keyboardSystemOverlayBlacklistState(
    @Given blacklistPrefs: Flow<SystemOverlayBlacklistPrefs>,
    @Given keyboardVisible: Flow<KeyboardVisible>,
    @Given logger: Logger,
): Flow<KeyboardSystemOverlayBlacklistState> = blacklistPrefs
    .map { it.disableOnKeyboard }
    .distinctUntilChanged()
    .flatMapLatest { disableOnKeyboard ->
        if (disableOnKeyboard) {
            keyboardVisible
                .map {
                    if (it) {
                        logger.d { "hide: keyboard" }
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

enum class SystemOverlayBlacklistState {
    DISABLED, ENABLED, HIDDEN
}
