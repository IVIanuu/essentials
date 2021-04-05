package com.ivianuu.essentials.hidenavbar.ui

import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.ui.res.stringResource
import com.ivianuu.essentials.hidenavbar.R
import com.ivianuu.essentials.store.ScopeStateStore
import com.ivianuu.essentials.store.State
import com.ivianuu.essentials.ui.dialog.Dialog
import com.ivianuu.essentials.ui.dialog.DialogKeyUiOptionsFactory
import com.ivianuu.essentials.ui.dialog.DialogWrapper
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.KeyUiGivenScope
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.ui.navigation.UrlKey
import com.ivianuu.essentials.ui.navigation.StateKeyUi
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.scope.Scoped
import kotlinx.coroutines.flow.StateFlow

class NavBarUnsupportedKey : Key<Nothing>

@Given
val navBarUnsupportedUi: StateKeyUi<NavBarUnsupportedKey, NavBarUnsupportedViewModel,
        NavBarUnsupportedState> = { viewModel, _ ->
    DialogWrapper {
        Dialog(
            title = {
                Text(stringResource(R.string.es_nav_bar_unsupported_title))
            },
            content = {
                Text(stringResource(R.string.es_nav_bar_unsupported_content))
            },
            neutralButton = {
                TextButton(onClick = { viewModel.openMoreInfos() }) {
                    Text(stringResource(R.string.es_more_infos))
                }
            },
            positiveButton = {
                TextButton(onClick = { viewModel.close() }) {
                    Text(stringResource(R.string.es_close))
                }
            }
        )
    }
}

@Given
val navBarUnsupportedOptions = DialogKeyUiOptionsFactory<NavBarUnsupportedKey>()

class NavBarUnsupportedState : State()

@Scoped<KeyUiGivenScope>
@Given
class NavBarUnsupportedViewModel(
    @Given private val key: NavBarUnsupportedKey,
    @Given private val navigator: Navigator,
    @Given private val store: ScopeStateStore<KeyUiGivenScope, NavBarUnsupportedState>
) : StateFlow<NavBarUnsupportedState> by store {
    fun openMoreInfos() = store.effect {
        navigator.push(
            UrlKey(
                "https://www.xda-developers.com/google-confirms-overscan-gone-android-11-crippling-third-party-gesture-apps/"
            )
        )
    }
    fun close() = store.effect { navigator.pop(key) }
}
