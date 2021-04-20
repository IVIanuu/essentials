package com.ivianuu.essentials.hidenavbar.ui

import androidx.compose.material.*
import androidx.compose.ui.res.*
import com.ivianuu.essentials.coroutines.*
import com.ivianuu.essentials.hidenavbar.R
import com.ivianuu.essentials.optics.*
import com.ivianuu.essentials.store.*
import com.ivianuu.essentials.ui.dialog.*
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.scope.*
import kotlinx.coroutines.flow.*

object NavBarUnsupportedKey : Key<Nothing>

@Given
val navBarUnsupportedUi: ModelKeyUi<NavBarUnsupportedKey, NavBarUnsupportedModel> = {
    DialogScaffold {
        Dialog(
            title = {
                Text(stringResource(R.string.es_nav_bar_unsupported_title))
            },
            neutralButton = {
                TextButton(onClick = model.openMoreInfos) {
                    Text(stringResource(R.string.es_more_infos))
                }
            },
            positiveButton = {
                TextButton(onClick = model.close) {
                    Text(stringResource(R.string.es_close))
                }
            }
        ) {
            Text(stringResource(R.string.es_nav_bar_unsupported_content))
        }
    }
}

@Given
val navBarUnsupportedOptions = DialogKeyUiOptionsFactory<NavBarUnsupportedKey>()

@Optics
data class NavBarUnsupportedModel(
    val openMoreInfos: () -> Unit = {},
    val close: () -> Unit = {}
)

@Given
fun navBarUnsupportedModel(
    @Given key: NavBarUnsupportedKey,
    @Given navigator: Navigator,
    @Given scope: ScopeCoroutineScope<KeyUiGivenScope>
): @Scoped<KeyUiGivenScope> StateFlow<NavBarUnsupportedModel> = scope.state(
    NavBarUnsupportedModel()
) {
    action(NavBarUnsupportedModel.openMoreInfos()) {
        navigator.push(
            UrlKey(
                "https://www.xda-developers.com/google-confirms-overscan-gone-android-11-crippling-third-party-gesture-apps/"
            )
        )
    }
    action(NavBarUnsupportedModel.close()) { navigator.pop(key) }
}
