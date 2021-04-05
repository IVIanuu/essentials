package com.ivianuu.essentials.systemoverlay.blacklist

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Text
import androidx.compose.ui.res.stringResource
import com.ivianuu.essentials.android.prefs.Pref
import com.ivianuu.essentials.coroutines.updateIn
import com.ivianuu.essentials.store.Initial
import com.ivianuu.essentials.store.ScopeStateStore
import com.ivianuu.essentials.store.State
import com.ivianuu.essentials.systemoverlay.R
import com.ivianuu.essentials.ui.core.localVerticalInsetsPadding
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.KeyUiGivenScope
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.ui.navigation.ViewModelKeyUi
import com.ivianuu.essentials.ui.prefs.CheckboxListItem
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.scope.Scoped
import kotlinx.coroutines.flow.StateFlow

class SystemOverlayBlacklistKey(val systemOverlayName: String) : Key<Nothing>

@Given
val systemOverlayBlacklistUi: ViewModelKeyUi<SystemOverlayBlacklistKey, SystemOverlayBlacklistViewModel,
        SystemOverlayBlacklistUiState> = { viewModel, state ->
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
                    onClick = { viewModel.openAppBlacklistSettings() }
                )
            }

            item {
                CheckboxListItem(
                    value = state.disableOnKeyboard,
                    onValueChange = { viewModel.updateDisableOnKeyboard(it) },
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
                    onValueChange = { viewModel.updateDisableOnLockScreen(it) },
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
                    onValueChange = { viewModel.updateDisableOnSecureScreens(it) },
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
) : State() {
    companion object {
        @Given
        fun initial(
            @Given key: SystemOverlayBlacklistKey
        ): @Initial SystemOverlayBlacklistUiState = SystemOverlayBlacklistUiState(
            systemOverlayName = key.systemOverlayName
        )
    }
}

@Scoped<KeyUiGivenScope>
@Given
class SystemOverlayBlacklistViewModel(
    @Given private val navigator: Navigator,
    @Given private val pref: Pref<SystemOverlayBlacklistPrefs>,
    @Given private val store: ScopeStateStore<KeyUiGivenScope, SystemOverlayBlacklistUiState>
) : StateFlow<SystemOverlayBlacklistUiState> by store {
    init {
        pref
            .updateIn(store) {
                copy(
                    disableOnKeyboard = it.disableOnKeyboard,
                    disableOnLockScreen = it.disableOnLockScreen,
                    disableOnSecureScreens = it.disableOnSecureScreens
                )
            }
    }
    fun openAppBlacklistSettings() = store.effect {
        navigator.push(SystemOverlayAppBlacklistKey())
    }
    fun updateDisableOnKeyboard(value: Boolean) = store.effect {
        pref.update { copy(disableOnKeyboard = value) }
    }
    fun updateDisableOnLockScreen(value: Boolean) = store.effect {
        pref.update { copy(disableOnLockScreen = value) }
    }
    fun updateDisableOnSecureScreens(value: Boolean) = store.effect {
        pref.update { copy(disableOnSecureScreens = value) }
    }
}
