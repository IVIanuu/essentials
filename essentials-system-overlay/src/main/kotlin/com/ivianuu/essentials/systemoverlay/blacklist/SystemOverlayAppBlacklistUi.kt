package com.ivianuu.essentials.systemoverlay.blacklist

import androidx.compose.runtime.remember
import com.ivianuu.essentials.apps.ui.DefaultAppFilter
import com.ivianuu.essentials.apps.ui.checkableapps.CheckableAppsParams
import com.ivianuu.essentials.apps.ui.checkableapps.CheckableAppsScreen
import com.ivianuu.essentials.data.ValueAction
import com.ivianuu.essentials.data.updateAndAwait
import com.ivianuu.essentials.store.Store
import com.ivianuu.essentials.store.StoreBuilder
import com.ivianuu.essentials.store.onAction
import com.ivianuu.essentials.systemoverlay.R
import com.ivianuu.essentials.systemoverlay.blacklist.SystemOverlayAppBlacklistAction.UpdateAppBlacklist
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.KeyUiGivenScope
import com.ivianuu.essentials.ui.navigation.StoreKeyUi
import com.ivianuu.essentials.util.ResourceProvider
import com.ivianuu.injekt.Given
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.map

class SystemOverlayAppBlacklistKey : Key<Nothing>

@Given
fun systemOverlayAppBlacklistUi(
    @Given checkableAppsPageFactory: (@Given CheckableAppsParams) -> CheckableAppsScreen,
    @Given resourceProvider: ResourceProvider
): StoreKeyUi<SystemOverlayAppBlacklistKey, SystemOverlayAppBlacklistState, SystemOverlayAppBlacklistAction> = {
    remember {
        checkableAppsPageFactory(
            CheckableAppsParams(
                checkedApps = state.appBlacklist,
                onCheckedAppsChanged = { send(UpdateAppBlacklist(it)) },
                appFilter = DefaultAppFilter,
                appBarTitle = resourceProvider.string(R.string.es_system_overlay_blacklist_title)
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
    @Given prefStore: Store<SystemOverlayBlacklistPrefs, ValueAction<SystemOverlayBlacklistPrefs>>
): StoreBuilder<KeyUiGivenScope, SystemOverlayAppBlacklistState, SystemOverlayAppBlacklistAction> = {
    update { copy(appBlacklist = prefStore.map { it.appBlacklist }) }
    onAction<UpdateAppBlacklist> { action ->
        prefStore.updateAndAwait { copy(appBlacklist = action.appBlacklist) }
    }
}
