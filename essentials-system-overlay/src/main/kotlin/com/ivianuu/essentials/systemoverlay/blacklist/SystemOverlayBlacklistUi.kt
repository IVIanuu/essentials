package com.ivianuu.essentials.systemoverlay.blacklist

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Text
import androidx.compose.ui.res.stringResource
import com.ivianuu.essentials.data.DataStore
import com.ivianuu.essentials.store.Initial
import com.ivianuu.essentials.store.StoreBuilder
import com.ivianuu.essentials.store.action
import com.ivianuu.essentials.store.updateIn
import com.ivianuu.essentials.systemoverlay.R
import com.ivianuu.essentials.systemoverlay.blacklist.SystemOverlayBlacklistAction.OpenAppBlacklistSettings
import com.ivianuu.essentials.systemoverlay.blacklist.SystemOverlayBlacklistAction.UpdateDisableOnKeyboard
import com.ivianuu.essentials.systemoverlay.blacklist.SystemOverlayBlacklistAction.UpdateDisableOnLockScreen
import com.ivianuu.essentials.systemoverlay.blacklist.SystemOverlayBlacklistAction.UpdateDisableOnSecureScreens
import com.ivianuu.essentials.ui.core.localVerticalInsetsPadding
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.KeyUiGivenScope
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.ui.navigation.StoreKeyUi
import com.ivianuu.essentials.ui.prefs.CheckboxListItem
import com.ivianuu.injekt.Given

class SystemOverlayBlacklistKey(val systemOverlayName: String) : Key<Nothing>

@Given
val systemOverlayBlacklistUi: StoreKeyUi<SystemOverlayBlacklistKey, SystemOverlayBlacklistUiState,
        SystemOverlayBlacklistAction> = {
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
                    onClick = { send(OpenAppBlacklistSettings) }
                )
            }

            item {
                CheckboxListItem(
                    value = state.disableOnKeyboard,
                    onValueChange = { send(UpdateDisableOnKeyboard(it)) },
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
                    onValueChange = { send(UpdateDisableOnLockScreen(it)) },
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
                    onValueChange = { send(UpdateDisableOnSecureScreens(it)) },
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

sealed class SystemOverlayBlacklistAction {
    object OpenAppBlacklistSettings : SystemOverlayBlacklistAction()
    data class UpdateDisableOnKeyboard(val value: Boolean) : SystemOverlayBlacklistAction()
    data class UpdateDisableOnLockScreen(val value: Boolean) : SystemOverlayBlacklistAction()
    data class UpdateDisableOnSecureScreens(val value: Boolean) : SystemOverlayBlacklistAction()
}

@Given
fun systemOverlayBlacklistStore(
    @Given navigator: Navigator,
    @Given pref: DataStore<SystemOverlayBlacklistPrefs>,
): StoreBuilder<KeyUiGivenScope, SystemOverlayBlacklistUiState, SystemOverlayBlacklistAction> = {
    pref.data.updateIn(this) {
        copy(
            disableOnKeyboard = it.disableOnKeyboard,
            disableOnLockScreen = it.disableOnLockScreen,
            disableOnSecureScreens = it.disableOnSecureScreens
        )
    }
    action<OpenAppBlacklistSettings> {
        navigator.push(SystemOverlayAppBlacklistKey())
    }
    action<UpdateDisableOnKeyboard> {
        pref.updateData { copy(disableOnKeyboard = it.value) }
    }
    action<UpdateDisableOnLockScreen> {
        pref.updateData { copy(disableOnLockScreen = it.value) }
    }
    action<UpdateDisableOnSecureScreens> {
        pref.updateData { copy(disableOnSecureScreens = it.value) }
    }
}
