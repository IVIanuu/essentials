package com.ivianuu.essentials.systemoverlay.blacklist

import androidx.compose.runtime.remember
import com.ivianuu.essentials.apps.ui.DefaultAppFilter
import com.ivianuu.essentials.apps.ui.checkableapps.CheckableAppsParams
import com.ivianuu.essentials.apps.ui.checkableapps.CheckableAppsScreen
import com.ivianuu.essentials.data.DataStore
import com.ivianuu.essentials.store.ScopeStateStore
import com.ivianuu.essentials.store.State
import com.ivianuu.essentials.systemoverlay.R
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.KeyUiGivenScope
import com.ivianuu.essentials.ui.navigation.StateKeyUi
import com.ivianuu.essentials.util.ResourceProvider
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.scope.Scoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emptyFlow

class SystemOverlayAppBlacklistKey : Key<Nothing>

@Given
fun systemOverlayAppBlacklistUi(
    @Given checkableAppsPageFactory: (@Given CheckableAppsParams) -> CheckableAppsScreen,
    @Given resourceProvider: ResourceProvider
): StateKeyUi<SystemOverlayAppBlacklistKey, SystemOverlayAppBlacklistViewModel,
        SystemOverlayAppBlacklistState> = { viewModel, state ->
    remember {
        checkableAppsPageFactory(
            CheckableAppsParams(
                checkedApps = state.appBlacklist,
                onCheckedAppsChanged = { viewModel.updateAppBlacklist(it) },
                appFilter = DefaultAppFilter,
                appBarTitle = resourceProvider.string(R.string.es_system_overlay_blacklist_title)
            )
        )
    }.invoke()
}

data class SystemOverlayAppBlacklistState(val appBlacklist: Flow<Set<String>> = emptyFlow()) : State()

@Scoped<KeyUiGivenScope>
@Given
class SystemOverlayAppBlacklistViewModel(
    @Given private val pref: DataStore<SystemOverlayBlacklistPrefs>,
    @Given private val store: ScopeStateStore<KeyUiGivenScope, SystemOverlayAppBlacklistState>
) : StateFlow<SystemOverlayAppBlacklistState> by store {
    fun updateAppBlacklist(appBlacklist: Set<String>) = store.effect {
        pref.update {
            copy(appBlacklist = appBlacklist)
        }
    }
}
