/*
 * Copyright 2020 Manuel Wrage
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ivianuu.essentials.permission.writesecuresettings

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import com.ivianuu.essentials.coroutines.EventFlow
import com.ivianuu.essentials.permission.PermissionStateFactory
import com.ivianuu.essentials.permission.R
import com.ivianuu.essentials.permission.writesecuresettings.WriteSecureSettingsAction.GrantPermissionsViaRoot
import com.ivianuu.essentials.permission.writesecuresettings.WriteSecureSettingsAction.OpenPcInstructions
import com.ivianuu.essentials.result.onFailure
import com.ivianuu.essentials.result.runKatching
import com.ivianuu.essentials.shell.Shell
import com.ivianuu.essentials.store.Collector
import com.ivianuu.essentials.store.Initial
import com.ivianuu.essentials.store.state
import com.ivianuu.essentials.ui.core.localVerticalInsets
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.KeyModule
import com.ivianuu.essentials.ui.navigation.KeyUi
import com.ivianuu.essentials.ui.navigation.KeyUiGivenScope
import com.ivianuu.essentials.ui.navigation.NavigationAction
import com.ivianuu.essentials.util.BuildInfo
import com.ivianuu.essentials.util.ScopeCoroutineScope
import com.ivianuu.essentials.util.Toaster
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.scope.Scoped
import com.ivianuu.injekt.common.TypeKey
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

data class WriteSecureSettingsKey(
    val permissionKey: TypeKey<WriteSecureSettingsPermission>
) : Key<Boolean>

@Given
val writeSecureSettingsKeyModule = KeyModule<WriteSecureSettingsKey>()

@Given
fun writeSecureSettingsUi(
    @Given stateFlow: StateFlow<WriteSecureSettingsState>,
    @Given dispatch: Collector<WriteSecureSettingsAction>,
): KeyUi<WriteSecureSettingsKey> = {
    val state by stateFlow.collectAsState()
    Scaffold(
        topBar = { TopAppBar(title = { Text(stringResource(R.string.es_title_secure_settings)) }) }
    ) {
        LazyColumn(contentPadding = localVerticalInsets()) {
            item {
                SecureSettingsHeader(
                    stringResource(R.string.es_pref_secure_settings_header_summary)
                )

                ListItem(
                    title = { Text(stringResource(R.string.es_pref_use_pc)) },
                    subtitle = { Text(stringResource(R.string.es_pref_use_pc_summary)) },
                    onClick = { dispatch(OpenPcInstructions) }
                )

                ListItem(
                    title = { Text(stringResource(R.string.es_pref_use_root)) },
                    subtitle = { Text(stringResource(R.string.es_pref_use_root_summary)) },
                    onClick = { dispatch(GrantPermissionsViaRoot) }
                )
            }
        }
    }
}

object WriteSecureSettingsState

sealed class WriteSecureSettingsAction {
    object GrantPermissionsViaRoot : WriteSecureSettingsAction()
    object OpenPcInstructions : WriteSecureSettingsAction()
}

@Scoped<KeyUiGivenScope>
@Given
fun writeSecureSettingsState(
    @Given scope: ScopeCoroutineScope<KeyUiGivenScope>,
    @Given initial: @Initial WriteSecureSettingsState = WriteSecureSettingsState,
    @Given actions: Flow<WriteSecureSettingsAction>,
    @Given buildInfo: BuildInfo,
    @Given key: WriteSecureSettingsKey,
    @Given navigator: Collector<NavigationAction>,
    @Given permissionStateFactory: PermissionStateFactory,
    @Given shell: Shell,
    @Given toaster: Toaster
): StateFlow<WriteSecureSettingsState> = scope.state(initial) {
    launch {
        val state = permissionStateFactory(listOf(key.permissionKey))
        while (coroutineContext.isActive) {
            if (state.first()) {
                toaster.showToast(R.string.es_secure_settings_permission_granted)
                navigator(NavigationAction.Pop(key, true))
                break
            }
            delay(200)
        }
    }
    actions
        .onEach { action ->
            when (action) {
                OpenPcInstructions -> navigator(
                    NavigationAction.Push(WriteSecureSettingsPcInstructionsKey(key.permissionKey))
                )
                GrantPermissionsViaRoot -> runKatching {
                    shell.run("pm grant ${buildInfo.packageName} android.permission.WRITE_SECURE_SETTINGS")
                }
                    .onFailure {
                        it.printStackTrace()
                        toaster.showToast(R.string.es_secure_settings_no_root)
                    }
            }
        }
        .launchIn(this)
}

@Scoped<KeyUiGivenScope>
@Given
val writeSecureSettingsActions get() = EventFlow<WriteSecureSettingsAction>()
