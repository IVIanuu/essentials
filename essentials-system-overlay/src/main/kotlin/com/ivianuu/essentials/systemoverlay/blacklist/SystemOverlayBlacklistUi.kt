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
import com.ivianuu.essentials.ui.navigation.ModelKeyUi
import com.ivianuu.essentials.ui.prefs.CheckboxListItem
import com.ivianuu.injekt.Given

class SystemOverlayBlacklistKey(val systemOverlayName: String) : Key<Nothing>

@Given
val systemOverlayBlacklistUi: ModelKeyUi<SystemOverlayBlacklistKey, SystemOverlayBlacklistModel> = {
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
                                model.systemOverlayName
                            )
                        )
                    },
                    onClick = model.openAppBlacklistSettings
                )
            }

            item {
                CheckboxListItem(
                    value = model.disableOnKeyboard,
                    onValueChange = model.updateDisableOnKeyboard,
                    title = { Text(stringResource(R.string.es_pref_disable_on_keyboard)) },
                    subtitle = {
                        Text(
                            stringResource(
                                R.string.es_pref_disable_on_keyboard_summary,
                                model.systemOverlayName
                            )
                        )
                    }
                )
            }

            item {
                CheckboxListItem(
                    value = model.disableOnLockScreen,
                    onValueChange = model.updateDisableOnLockScreen,
                    title = { Text(stringResource(R.string.es_pref_disable_on_lock_screen)) },
                    subtitle = {
                        Text(
                            stringResource(
                                R.string.es_pref_disable_on_lock_screen_summary,
                                model.systemOverlayName
                            )
                        )
                    }
                )
            }

            item {
                CheckboxListItem(
                    value = model.disableOnSecureScreens,
                    onValueChange = model.updateDisableOnSecureScreens,
                    title = { Text(stringResource(R.string.es_pref_disable_on_secure_screens)) },
                    subtitle = {
                        Text(
                            stringResource(
                                R.string.es_pref_disable_on_secure_screens_summary,
                                model.systemOverlayName
                            )
                        )
                    }
                )
            }
        }
    }
}

@Optics
data class SystemOverlayBlacklistModel(
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
        ): @Initial SystemOverlayBlacklistModel = SystemOverlayBlacklistModel(
            systemOverlayName = key.systemOverlayName
        )
    }
}

@Given
fun systemOverlayBlacklistModel(
    @Given navigator: Navigator,
    @Given pref: DataStore<SystemOverlayBlacklistPrefs>,
): StateBuilder<KeyUiGivenScope, SystemOverlayBlacklistModel> = {
    pref.data.updateIn(this) {
        copy(
            disableOnKeyboard = it.disableOnKeyboard,
            disableOnLockScreen = it.disableOnLockScreen,
            disableOnSecureScreens = it.disableOnSecureScreens
        )
    }
    action(SystemOverlayBlacklistModel.openAppBlacklistSettings()) {
        navigator.push(SystemOverlayAppBlacklistKey())
    }
    action(SystemOverlayBlacklistModel.updateDisableOnKeyboard()) { value ->
        pref.updateData { copy(disableOnKeyboard = value) }
    }
    action(SystemOverlayBlacklistModel.updateDisableOnLockScreen()) { value ->
        pref.updateData { copy(disableOnLockScreen = value) }
    }
    action(SystemOverlayBlacklistModel.updateDisableOnSecureScreens()) { value ->
        pref.updateData { copy(disableOnSecureScreens = value) }
    }
}
