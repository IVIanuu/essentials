package com.ivianuu.essentials.hidenavbar.ui

import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.ui.res.stringResource
import com.ivianuu.essentials.hidenavbar.R
import com.ivianuu.essentials.hidenavbar.ui.NavBarUnsupportedAction.*
import com.ivianuu.essentials.store.FeatureBuilder
import com.ivianuu.essentials.store.State
import com.ivianuu.essentials.store.actionsOf
import com.ivianuu.essentials.store.collectIn
import com.ivianuu.essentials.ui.dialog.Dialog
import com.ivianuu.essentials.ui.dialog.DialogKeyUiOptionsFactory
import com.ivianuu.essentials.ui.dialog.DialogWrapper
import com.ivianuu.essentials.ui.navigation.FeatureKeyUi
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.KeyUiGivenScope
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.ui.navigation.UrlKey
import com.ivianuu.injekt.Given

class NavBarUnsupportedKey : Key<Nothing>

@Given
val navBarUnsupportedUi: FeatureKeyUi<NavBarUnsupportedKey, NavBarUnsupportedState,
        NavBarUnsupportedAction> = { _, collector ->
    DialogWrapper {
        Dialog(
            title = {
                Text(stringResource(R.string.es_nav_bar_unsupported_title))
            },
            content = {
                Text(stringResource(R.string.es_nav_bar_unsupported_content))
            },
            neutralButton = {
                TextButton(onClick = { collector.tryEmit(OpenMoreInfos) }) {
                    Text(stringResource(R.string.es_more_infos))
                }
            },
            positiveButton = {
                TextButton(onClick = { collector.tryEmit(Close) }) {
                    Text(stringResource(R.string.es_close))
                }
            }
        )
    }
}

@Given
val navBarUnsupportedOptions = DialogKeyUiOptionsFactory<NavBarUnsupportedKey>()

class NavBarUnsupportedState : State()

sealed class NavBarUnsupportedAction {
    object OpenMoreInfos : NavBarUnsupportedAction()
    object Close : NavBarUnsupportedAction()
}

@Given
fun navBarUnsupportedFeature(
    @Given key: NavBarUnsupportedKey,
    @Given navigator: Navigator
): FeatureBuilder<KeyUiGivenScope, NavBarUnsupportedState, NavBarUnsupportedAction> = {
    actionsOf<OpenMoreInfos>()
        .collectIn(this) {
            navigator.push(
                UrlKey(
                    "https://www.xda-developers.com/google-confirms-overscan-gone-android-11-crippling-third-party-gesture-apps/"
                )
            )
        }
    actionsOf<Close>()
        .collectIn(this) { navigator.pop(key) }
}
