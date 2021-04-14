package com.ivianuu.essentials.systemoverlay.blacklist

import androidx.compose.runtime.remember
import com.ivianuu.essentials.apps.ui.DefaultAppFilter
import com.ivianuu.essentials.apps.ui.checkableapps.CheckableAppsParams
import com.ivianuu.essentials.apps.ui.checkableapps.CheckableAppsScreen
import com.ivianuu.essentials.data.DataStore
import com.ivianuu.essentials.store.StoreBuilder
import com.ivianuu.essentials.store.onAction
import com.ivianuu.essentials.systemoverlay.R
import com.ivianuu.essentials.systemoverlay.blacklist.SystemOverlayAppBlacklistAction.UpdateAppBlacklist
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.KeyUiGivenScope
import com.ivianuu.essentials.ui.navigation.StoreKeyUi
import com.ivianuu.essentials.util.LoadStringResourceUseCase
import com.ivianuu.injekt.Given
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.map

class SystemOverlayAppBlacklistKey : Key<Nothing>

@Given
fun systemOverlayAppBlacklistUi(
    @Given checkableAppsPageFactory: (@Given CheckableAppsParams) -> CheckableAppsScreen,
    @Given stringResource: LoadStringResourceUseCase
): StoreKeyUi<SystemOverlayAppBlacklistKey, SystemOverlayAppBlacklistState, SystemOverlayAppBlacklistAction> = {
    remember {
        checkableAppsPageFactory(
            CheckableAppsParams(
                checkedApps = state.appBlacklist,
                onCheckedAppsChanged = { send(UpdateAppBlacklist(it)) },
                appFilter = DefaultAppFilter,
                appBarTitle = stringResource(R.string.es_system_overlay_blacklist_title, emptyList())
            )
        )
    }.invoke()
}

data class SystemOverlayAppBlacklistState(val appBlacklist: Flow<Set<String>> = emptyFlow())

sealed class SystemOverlayAppBlacklistAction {
    data class UpdateAppBlacklist(val appBlacklist: Set<String>) : SystemOverlayAppBlacklistAction()
}

@Given
fun systemOverlayAppBlacklistStore(
    @Given pref: DataStore<SystemOverlayBlacklistPrefs>
): StoreBuilder<KeyUiGivenScope, SystemOverlayAppBlacklistState, SystemOverlayAppBlacklistAction> = {
    update { copy(appBlacklist = pref.data.map { it.appBlacklist }) }
    onAction<UpdateAppBlacklist> { action ->
        pref.updateData { copy(appBlacklist = action.appBlacklist) }
    }
}
