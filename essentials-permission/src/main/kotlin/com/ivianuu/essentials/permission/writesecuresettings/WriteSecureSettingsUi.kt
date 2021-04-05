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
import androidx.compose.ui.res.stringResource
import com.github.michaelbull.result.onFailure
import com.github.michaelbull.result.runCatching
import com.ivianuu.essentials.permission.PermissionStateFactory
import com.ivianuu.essentials.permission.R
import com.ivianuu.essentials.shell.Shell
import com.ivianuu.essentials.store.ScopeStateStore
import com.ivianuu.essentials.store.State
import com.ivianuu.essentials.ui.core.localVerticalInsetsPadding
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.KeyUiGivenScope
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.ui.navigation.ViewModelKeyUi
import com.ivianuu.essentials.util.BuildInfo
import com.ivianuu.essentials.util.Toaster
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.common.TypeKey
import kotlin.coroutines.coroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.isActive

class WriteSecureSettingsKey(
    val permissionKey: TypeKey<WriteSecureSettingsPermission>
) : Key<Boolean>

@Given
val writeSecureSettingsUi: ViewModelKeyUi<WriteSecureSettingsKey, WriteSecureSettingsViewModel,
        WriteSecureSettingsState> = { viewModel, state ->
    Scaffold(
        topBar = { TopAppBar(title = { Text(stringResource(R.string.es_title_secure_settings)) }) }
    ) {
        LazyColumn(contentPadding = localVerticalInsetsPadding()) {
            item {
                SecureSettingsHeader(
                    stringResource(R.string.es_pref_secure_settings_header_summary)
                )
            }
            item {
                ListItem(
                    title = { Text(stringResource(R.string.es_pref_use_pc)) },
                    subtitle = { Text(stringResource(R.string.es_pref_use_pc_summary)) },
                    onClick = { viewModel.openPcInstructions() }
                )
            }
            item {
                ListItem(
                    title = { Text(stringResource(R.string.es_pref_use_root)) },
                    subtitle = { Text(stringResource(R.string.es_pref_use_root_summary)) },
                    onClick = { viewModel.grantPermissionsViaRoot() }
                )
            }
        }
    }
}

object WriteSecureSettingsState : State()

@Given
class WriteSecureSettingsViewModel(
    @Given private val buildInfo: BuildInfo,
    @Given private val key: WriteSecureSettingsKey,
    @Given private val navigator: Navigator,
    @Given private val permissionStateFactory: PermissionStateFactory,
    @Given private val shell: Shell,
    @Given private val toaster: Toaster,
    @Given private val store: ScopeStateStore<KeyUiGivenScope, WriteSecureSettingsState>
) : StateFlow<WriteSecureSettingsState> by store {
    init {
        store.effect {
            val state = permissionStateFactory(listOf(key.permissionKey))
            while (coroutineContext.isActive) {
                if (state.first()) {
                    toaster.showToast(R.string.es_secure_settings_permission_granted)
                    navigator.pop(key, true)
                    break
                }
                delay(200)
            }
        }
    }
    fun openPcInstructions() = store.effect {
        navigator.push(WriteSecureSettingsPcInstructionsKey(key.permissionKey))
    }
    fun grantPermissionsViaRoot() = store.effect {
        runCatching {
            shell.run("pm grant ${buildInfo.packageName} android.permission.WRITE_SECURE_SETTINGS")
        }.onFailure {
            it.printStackTrace()
            toaster.showToast(R.string.es_secure_settings_no_root)
        }
    }
}
