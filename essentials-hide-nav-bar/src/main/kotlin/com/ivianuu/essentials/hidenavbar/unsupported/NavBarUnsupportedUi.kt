package com.ivianuu.essentials.hidenavbar.unsupported

import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import com.ivianuu.essentials.coroutines.EventFlow
import com.ivianuu.essentials.hidenavbar.R
import com.ivianuu.essentials.hidenavbar.unsupported.NavBarUnsupportedAction.Close
import com.ivianuu.essentials.hidenavbar.unsupported.NavBarUnsupportedAction.OpenMoreInfos
import com.ivianuu.essentials.store.Collector
import com.ivianuu.essentials.store.Initial
import com.ivianuu.essentials.store.state
import com.ivianuu.essentials.ui.dialog.Dialog
import com.ivianuu.essentials.ui.dialog.DialogKeyUiOptionsFactory
import com.ivianuu.essentials.ui.dialog.DialogWrapper
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.KeyModule
import com.ivianuu.essentials.ui.navigation.KeyUi
import com.ivianuu.essentials.ui.navigation.KeyUiComponent
import com.ivianuu.essentials.ui.navigation.KeyUiScope
import com.ivianuu.essentials.ui.navigation.NavigationAction
import com.ivianuu.essentials.ui.navigation.NavigationAction.Pop
import com.ivianuu.essentials.ui.navigation.NavigationAction.Push
import com.ivianuu.essentials.ui.navigation.UrlKey
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.Module
import com.ivianuu.injekt.common.Scoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class NavBarUnsupportedKey : Key<Nothing>

@Module
val navBarUnsupportedKeyModule = KeyModule<NavBarUnsupportedKey>()

@Given
fun navBarUnsupportedUi(
    @Given stateFlow: StateFlow<NavBarUnsupportedState>,
    @Given dispatch: Collector<NavBarUnsupportedAction>
): KeyUi<NavBarUnsupportedKey> = {
    val state by stateFlow.collectAsState()
    DialogWrapper {
        Dialog(
            title = {
                Text(stringResource(R.string.es_nav_bar_unsupported_title))
            },
            content = {
                Text(stringResource(R.string.es_nav_bar_unsupported_content))
            },
            neutralButton = {
                TextButton(onClick = { dispatch(OpenMoreInfos) }) {
                    Text(stringResource(R.string.es_more_infos))
                }
            },
            positiveButton = {
                TextButton(onClick = { dispatch(Close) }) {
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

@Scoped<KeyUiComponent>
@Given
fun navBarUnsupportedState(
    @Given scope: KeyUiScope,
    @Given initial: @Initial NavBarUnsupportedState = NavBarUnsupportedState(),
    @Given actions: Flow<NavBarUnsupportedAction>,
    @Given key: NavBarUnsupportedKey,
    @Given navigator: Collector<NavigationAction>
): StateFlow<NavBarUnsupportedState> = scope.state(initial) {
    actions
        .filterIsInstance<OpenMoreInfos>()
        .onEach {
            navigator(
                Push(
                    UrlKey(
                        "https://www.xda-developers.com/google-confirms-overscan-gone-android-11-crippling-third-party-gesture-apps/"
                    )
                )
            )
        }
        .launchIn(this)

    actions
        .filterIsInstance<Close>()
        .onEach { navigator(Pop(key)) }
        .launchIn(this)
}

@Scoped<KeyUiComponent>
@Given
val navBarUnsupportedActions get() = EventFlow<NavBarUnsupportedAction>()
