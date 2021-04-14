package com.ivianuu.essentials.hidenavbar.ui

import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.ui.res.stringResource
import com.ivianuu.essentials.coroutines.collectIn
import com.ivianuu.essentials.hidenavbar.R
import com.ivianuu.essentials.hidenavbar.ui.NavBarUnsupportedAction.Close
import com.ivianuu.essentials.hidenavbar.ui.NavBarUnsupportedAction.OpenMoreInfos
import com.ivianuu.essentials.store.StoreBuilder
import com.ivianuu.essentials.store.actions
import com.ivianuu.essentials.ui.dialog.Dialog
import com.ivianuu.essentials.ui.dialog.DialogKeyUiOptionsFactory
import com.ivianuu.essentials.ui.dialog.DialogScaffold
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.KeyUiGivenScope
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.ui.navigation.StoreKeyUi
import com.ivianuu.essentials.ui.navigation.UrlKey
import com.ivianuu.injekt.Given

class NavBarUnsupportedKey : Key<Nothing>

@Given
val navBarUnsupportedUi: StoreKeyUi<NavBarUnsupportedKey, NavBarUnsupportedState,
        NavBarUnsupportedAction> = {
    DialogScaffold {
        Dialog(
            title = {
                Text(stringResource(R.string.es_nav_bar_unsupported_title))
            },
            content = {
                Text(stringResource(R.string.es_nav_bar_unsupported_content))
            },
            neutralButton = {
                TextButton(onClick = { send(OpenMoreInfos) }) {
                    Text(stringResource(R.string.es_more_infos))
                }
            },
            positiveButton = {
                TextButton(onClick = { send(Close) }) {
                    Text(stringResource(R.string.es_close))
                }
            }
        )
    }
}

@Given
val navBarUnsupportedOptions = DialogKeyUiOptionsFactory<NavBarUnsupportedKey>()

class NavBarUnsupportedState

sealed class NavBarUnsupportedAction {
    object OpenMoreInfos : NavBarUnsupportedAction()
    object Close : NavBarUnsupportedAction()
}

@Given
fun navBarUnsupportedStore(
    @Given key: NavBarUnsupportedKey,
    @Given navigator: Navigator
): StoreBuilder<KeyUiGivenScope, NavBarUnsupportedState, NavBarUnsupportedAction> = {
    actions<OpenMoreInfos>().collectIn(this) {
        navigator.push(
            UrlKey(
                "https://www.xda-developers.com/google-confirms-overscan-gone-android-11-crippling-third-party-gesture-apps/"
            )
        )
    }
    actions<Close>().collectIn(this) { navigator.pop(key) }
}
