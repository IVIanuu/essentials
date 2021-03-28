package com.ivianuu.essentials.systemoverlay.blacklist

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import com.ivianuu.essentials.android.prefs.PrefAction
import com.ivianuu.essentials.android.prefs.update
import com.ivianuu.essentials.coroutines.EventFlow
import com.ivianuu.essentials.store.Collector
import com.ivianuu.essentials.store.Initial
import com.ivianuu.essentials.store.state
import com.ivianuu.essentials.systemoverlay.R
import com.ivianuu.essentials.systemoverlay.blacklist.SystemOverlayBlacklistUiAction.*
import com.ivianuu.essentials.ui.core.localVerticalInsetsPadding
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.KeyModule
import com.ivianuu.essentials.ui.navigation.KeyUi
import com.ivianuu.essentials.ui.navigation.KeyUiGivenScope
import com.ivianuu.essentials.ui.navigation.NavigationAction
import com.ivianuu.essentials.ui.prefs.CheckboxListItem
import com.ivianuu.essentials.util.ScopeCoroutineScope
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.scope.Scoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class SystemOverlayBlacklistKey(val systemOverlayName: String) : Key<Nothing>

@Given
val systemOverlayBlacklistKeyModule = KeyModule<SystemOverlayBlacklistKey>()

@Given
fun systemOverlayBlacklistUi(
    @Given stateFlow: StateFlow<SystemOverlayBlacklistUiState>,
    @Given dispatch: Collector<SystemOverlayBlacklistUiAction>
): KeyUi<SystemOverlayBlacklistKey> = {
    val state by stateFlow.collectAsState()
    Scaffold(
        topBar = { TopAppBar(title = { Text(stringResource(R.string.es_system_overlay_blacklist_title)) }) }
    ) {
        LazyColumn(contentPadding = localVerticalInsetsPadding()) {
            item {
                ListItem(
                    title = {
                        Text(
                            stringResource(
                                R.string.es_pref_system_overlay_app_blacklist,
                                state.systemOverlayName
                            )
                        )
                    },
                    onClick = { dispatch(OpenAppBlacklistSettings) }
                )
            }

            item {
                CheckboxListItem(
                    value = state.disableOnKeyboard,
                    onValueChange = { dispatch(UpdateDisableOnKeyboard(it)) },
                    title = { Text(stringResource(R.string.es_pref_disable_on_keyboard)) },
                    subtitle = {
                        Text(
                            stringResource(
                                R.string.es_pref_disable_on_keyboard_summary,
                                state.systemOverlayName
                            )
                        )
                    }
                )
            }

            item {
                CheckboxListItem(
                    value = state.disableOnLockScreen,
                    onValueChange = { dispatch(UpdateDisableOnLockScreen(it)) },
                    title = { Text(stringResource(R.string.es_pref_disable_on_lock_screen)) },
                    subtitle = {
                        Text(
                            stringResource(
                                R.string.es_pref_disable_on_lock_screen_summary,
                                state.systemOverlayName
                            )
                        )
                    }
                )
            }

            item {
                CheckboxListItem(
                    value = state.disableOnSecureScreens,
                    onValueChange = { dispatch(UpdateDisableOnSecureScreens(it)) },
                    title = { Text(stringResource(R.string.es_pref_disable_on_secure_screens)) },
                    subtitle = {
                        Text(
                            stringResource(
                                R.string.es_pref_disable_on_secure_screens_summary,
                                state.systemOverlayName
                            )
                        )
                    }
                )
            }
        }
    }
}

data class SystemOverlayBlacklistUiState(
    val systemOverlayName: String,
    val disableOnKeyboard: Boolean = false,
    val disableOnLockScreen: Boolean = false,
    val disableOnSecureScreens: Boolean = false
)

@Given
fun initialSystemOverlayBlacklistUiState(
    @Given key: SystemOverlayBlacklistKey
): @Initial SystemOverlayBlacklistUiState = SystemOverlayBlacklistUiState(
    systemOverlayName = key.systemOverlayName
)

sealed class SystemOverlayBlacklistUiAction {
    object OpenAppBlacklistSettings : SystemOverlayBlacklistUiAction()
    data class UpdateDisableOnKeyboard(val value: Boolean) : SystemOverlayBlacklistUiAction()
    data class UpdateDisableOnLockScreen(val value: Boolean) : SystemOverlayBlacklistUiAction()
    data class UpdateDisableOnSecureScreens(val value: Boolean) : SystemOverlayBlacklistUiAction()
}

@Given
fun systemOverlayBlacklistUiState(
    @Given scope: ScopeCoroutineScope<KeyUiGivenScope>,
    @Given initial: @Initial SystemOverlayBlacklistUiState,
    @Given actions: Flow<SystemOverlayBlacklistUiAction>,
    @Given navigator: Collector<NavigationAction>,
    @Given prefs: Flow<SystemOverlayBlacklistPrefs>,
    @Given prefUpdater: Collector<PrefAction<SystemOverlayBlacklistPrefs>>
): @Scoped<KeyUiGivenScope> StateFlow<SystemOverlayBlacklistUiState> = scope.state(initial) {
    prefs
        .reduce {
            copy(
                disableOnKeyboard = it.disableOnKeyboard,
                disableOnLockScreen = it.disableOnLockScreen,
                disableOnSecureScreens = it.disableOnSecureScreens
            )
        }
        .launchIn(this)

    actions
        .filterIsInstance<OpenAppBlacklistSettings>()
        .onEach { navigator(NavigationAction.Push(SystemOverlayAppBlacklistKey())) }
        .launchIn(this)

    actions
        .filterIsInstance<UpdateDisableOnKeyboard>()
        .onEach { prefUpdater.update { copy(disableOnKeyboard = it.value) } }
        .launchIn(this)

    actions
        .filterIsInstance<UpdateDisableOnLockScreen>()
        .onEach { prefUpdater.update { copy(disableOnLockScreen = it.value) } }
        .launchIn(this)

    actions
        .filterIsInstance<UpdateDisableOnSecureScreens>()
        .onEach { prefUpdater.update { copy(disableOnSecureScreens = it.value) } }
        .launchIn(this)
}

@Given
val systemOverlayBlacklistUiActions: @Scoped<KeyUiGivenScope> MutableSharedFlow<SystemOverlayBlacklistUiAction>
    get() = EventFlow()
