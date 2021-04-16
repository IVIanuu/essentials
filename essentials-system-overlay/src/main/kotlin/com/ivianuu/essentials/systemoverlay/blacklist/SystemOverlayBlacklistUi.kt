package com.ivianuu.essentials.systemoverlay.blacklist

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Text
import androidx.compose.ui.res.stringResource
import com.ivianuu.essentials.data.DataStore
import com.ivianuu.essentials.optics.Optics
import com.ivianuu.essentials.store.Initial
import com.ivianuu.essentials.store.StateBuilder
import com.ivianuu.essentials.store.action
import com.ivianuu.essentials.store.updateIn
import com.ivianuu.essentials.systemoverlay.R
import com.ivianuu.essentials.ui.core.localVerticalInsetsPadding
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.KeyUiGivenScope
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.ui.navigation.StateKeyUi
import com.ivianuu.essentials.ui.prefs.CheckboxListItem
import com.ivianuu.injekt.Given

class SystemOverlayBlacklistKey(val systemOverlayName: String) : Key<Nothing>

@Given
val systemOverlayBlacklistUi: StateKeyUi<SystemOverlayBlacklistKey, SystemOverlayBlacklistUiState> = {
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
                    onClick = state.openAppBlacklistSettings
                )
            }

            item {
                CheckboxListItem(
                    value = state.disableOnKeyboard,
                    onValueChange = state.updateDisableOnKeyboard,
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
                    onValueChange = state.updateDisableOnLockScreen,
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
                    onValueChange = state.updateDisableOnSecureScreens,
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

@Optics
data class SystemOverlayBlacklistUiState(
    val systemOverlayName: String,
    val disableOnKeyboard: Boolean = false,
    val disableOnLockScreen: Boolean = false,
    val disableOnSecureScreens: Boolean = false,
    val openAppBlacklistSettings: () -> Unit = {},
    val updateDisableOnKeyboard: (Boolean) -> Unit = {},
    val updateDisableOnLockScreen: (Boolean) -> Unit = {},
    val updateDisableOnSecureScreens: (Boolean) -> Unit = {}
) {
    companion object {
        @Given
        fun initial(
            @Given key: SystemOverlayBlacklistKey
        ): @Initial SystemOverlayBlacklistUiState = SystemOverlayBlacklistUiState(
            systemOverlayName = key.systemOverlayName
        )
    }
}

@Given
fun systemOverlayBlacklistState(
    @Given navigator: Navigator,
    @Given pref: DataStore<SystemOverlayBlacklistPrefs>,
): StateBuilder<KeyUiGivenScope, SystemOverlayBlacklistUiState> = {
    pref.data.updateIn(this) {
        copy(
            disableOnKeyboard = it.disableOnKeyboard,
            disableOnLockScreen = it.disableOnLockScreen,
            disableOnSecureScreens = it.disableOnSecureScreens
        )
    }
    action(SystemOverlayBlacklistUiState.openAppBlacklistSettings()) {
        navigator.push(SystemOverlayAppBlacklistKey())
    }
    action(SystemOverlayBlacklistUiState.updateDisableOnKeyboard()) { value ->
        pref.updateData { copy(disableOnKeyboard = value) }
    }
    action(SystemOverlayBlacklistUiState.updateDisableOnLockScreen()) { value ->
        pref.updateData { copy(disableOnLockScreen = value) }
    }
    action(SystemOverlayBlacklistUiState.updateDisableOnSecureScreens()) { value ->
        pref.updateData { copy(disableOnSecureScreens = value) }
    }
}
