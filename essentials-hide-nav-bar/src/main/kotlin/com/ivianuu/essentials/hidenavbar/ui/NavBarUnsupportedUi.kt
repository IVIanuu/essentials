package com.ivianuu.essentials.hidenavbar.ui

import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.ui.res.stringResource
import com.ivianuu.essentials.hidenavbar.R
import com.ivianuu.essentials.optics.Optics
import com.ivianuu.essentials.store.StateBuilder
import com.ivianuu.essentials.store.action
import com.ivianuu.essentials.ui.dialog.Dialog
import com.ivianuu.essentials.ui.dialog.DialogKeyUiOptionsFactory
import com.ivianuu.essentials.ui.dialog.DialogScaffold
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.KeyUiGivenScope
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.ui.navigation.StateKeyUi
import com.ivianuu.essentials.ui.navigation.UrlKey
import com.ivianuu.injekt.Given

class NavBarUnsupportedKey : Key<Nothing>

@Given
val navBarUnsupportedUi: StateKeyUi<NavBarUnsupportedKey, NavBarUnsupportedState> = {
    DialogScaffold {
        Dialog(
            title = {
                Text(stringResource(R.string.es_nav_bar_unsupported_title))
            },
            content = {
                Text(stringResource(R.string.es_nav_bar_unsupported_content))
            },
            neutralButton = {
                TextButton(onClick = state.openMoreInfos) {
                    Text(stringResource(R.string.es_more_infos))
                }
            },
            positiveButton = {
                TextButton(onClick = state.close) {
                    Text(stringResource(R.string.es_close))
                }
            }
        )
    }
}

@Given
val navBarUnsupportedOptions = DialogKeyUiOptionsFactory<NavBarUnsupportedKey>()

@Optics
data class NavBarUnsupportedState(
    val openMoreInfos: () -> Unit = {},
    val close: () -> Unit = {}
)

@Given
fun navBarUnsupportedState(
    @Given key: NavBarUnsupportedKey,
    @Given navigator: Navigator
): StateBuilder<KeyUiGivenScope, NavBarUnsupportedState> = {
    action(NavBarUnsupportedState.openMoreInfos()) {
        navigator.push(
            UrlKey(
                "https://www.xda-developers.com/google-confirms-overscan-gone-android-11-crippling-third-party-gesture-apps/"
            )
        )
    }
    action(NavBarUnsupportedState.close()) { navigator.pop(key) }
}
