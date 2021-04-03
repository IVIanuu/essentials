package com.ivianuu.essentials.systemoverlay.blacklist

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.ivianuu.essentials.android.prefs.PrefAction
import com.ivianuu.essentials.android.prefs.update
import com.ivianuu.essentials.apps.ui.DefaultAppFilter
import com.ivianuu.essentials.apps.ui.checkableapps.CheckableAppsParams
import com.ivianuu.essentials.apps.ui.checkableapps.CheckableAppsScreen
import com.ivianuu.essentials.coroutines.EventFlow
import com.ivianuu.essentials.store.Collector
import com.ivianuu.essentials.store.Initial
import com.ivianuu.essentials.store.state
import com.ivianuu.essentials.systemoverlay.R
import com.ivianuu.essentials.systemoverlay.blacklist.SystemOverlayAppBlacklistAction.UpdateAppBlacklist
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.KeyModule
import com.ivianuu.essentials.ui.navigation.KeyUi
import com.ivianuu.essentials.ui.navigation.KeyUiGivenScope
import com.ivianuu.essentials.util.ResourceProvider
import com.ivianuu.essentials.coroutines.ScopeCoroutineScope
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.scope.Scoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

class SystemOverlayAppBlacklistKey : Key<Nothing>

@Given
val systemOverlayAppBlacklistKeyModule = KeyModule<SystemOverlayAppBlacklistKey>()

@Given
fun systemOverlayAppBlacklistUi(
    @Given dispatch: Collector<SystemOverlayAppBlacklistAction>,
    @Given checkableAppsPageFactory: (@Given CheckableAppsParams) -> CheckableAppsScreen,
    @Given resourceProvider: ResourceProvider,
    @Given stateFlow: StateFlow<SystemOverlayAppBlacklistState>
): KeyUi<SystemOverlayAppBlacklistKey> = {
    val state by stateFlow.collectAsState()
    remember {
        checkableAppsPageFactory(
            CheckableAppsParams(
                checkedApps = state.appBlacklist,
                onCheckedAppsChanged = { dispatch(UpdateAppBlacklist(it)) },
                appFilter = DefaultAppFilter,
                appBarTitle = resourceProvider.string(R.string.es_system_overlay_blacklist_title)
            )
        )
    }()
}

data class SystemOverlayAppBlacklistState(val appBlacklist: Flow<Set<String>> = emptyFlow())

sealed class SystemOverlayAppBlacklistAction {
    data class UpdateAppBlacklist(val appBlacklist: Set<String>) : SystemOverlayAppBlacklistAction()
}

@Given
fun systemOverlayAppBlacklistState(
    @Given scope: ScopeCoroutineScope<KeyUiGivenScope>,
    @Given initial: @Initial SystemOverlayAppBlacklistState = SystemOverlayAppBlacklistState(),
    @Given actions: Flow<SystemOverlayAppBlacklistAction>,
    @Given prefs: Flow<SystemOverlayBlacklistPrefs>,
    @Given prefUpdater: Collector<PrefAction<SystemOverlayBlacklistPrefs>>
): @Scoped<KeyUiGivenScope> StateFlow<SystemOverlayAppBlacklistState> = scope.state(
    initial.copy(appBlacklist = prefs.map { it.appBlacklist })
) {
    actions
        .filterIsInstance<UpdateAppBlacklist>()
        .onEach { action ->
            prefUpdater.update {
                copy(appBlacklist = action.appBlacklist)
            }
        }
        .launchIn(this)
}

@Given
val systemOverlayAppBlacklistActions:
        @Scoped<KeyUiGivenScope> MutableSharedFlow<SystemOverlayAppBlacklistAction>
    get() = EventFlow()
