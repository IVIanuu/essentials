package com.ivianuu.essentials.systemoverlay.blacklist

import androidx.compose.runtime.remember
import com.ivianuu.essentials.apps.ui.DefaultAppPredicate
import com.ivianuu.essentials.apps.ui.checkableapps.CheckableAppsParams
import com.ivianuu.essentials.apps.ui.checkableapps.CheckableAppsScreen
import com.ivianuu.essentials.data.DataStore
import com.ivianuu.essentials.optics.Optics
import com.ivianuu.essentials.store.StateBuilder
import com.ivianuu.essentials.store.action
import com.ivianuu.essentials.systemoverlay.R
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.KeyUiGivenScope
import com.ivianuu.essentials.ui.navigation.ModelKeyUi
import com.ivianuu.essentials.util.StringResourceProvider
import com.ivianuu.injekt.Given
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.map

class SystemOverlayAppBlacklistKey : Key<Nothing>

@Given
fun systemOverlayAppBlacklistUi(
    @Given checkableAppsPageFactory: (@Given CheckableAppsParams) -> CheckableAppsScreen,
    @Given stringResource: StringResourceProvider
): ModelKeyUi<SystemOverlayAppBlacklistKey, SystemOverlayAppBlacklistState> = {
    remember {
        checkableAppsPageFactory(
            CheckableAppsParams(
                checkedApps = model.appBlacklist,
                onCheckedAppsChanged = model.updateAppBlacklist,
                appPredicate = DefaultAppPredicate,
                appBarTitle = stringResource(R.string.es_system_overlay_blacklist_title, emptyList())
            )
        )
    }.invoke()
}

@Optics
data class SystemOverlayAppBlacklistState(
    val appBlacklist: Flow<Set<String>> = emptyFlow(),
    val updateAppBlacklist: (Set<String>) -> Unit = {}
)

@Given
fun systemOverlayAppBlacklistState(
    @Given pref: DataStore<SystemOverlayBlacklistPrefs>
): StateBuilder<KeyUiGivenScope, SystemOverlayAppBlacklistState> = {
    update { copy(appBlacklist = pref.data.map { it.appBlacklist }) }
    action(SystemOverlayAppBlacklistState.updateAppBlacklist()) { appBlacklist ->
        pref.updateData { copy(appBlacklist = appBlacklist) }
    }
}
