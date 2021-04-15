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
import com.ivianuu.essentials.coroutines.timer
import com.ivianuu.essentials.permission.PermissionStateFactory
import com.ivianuu.essentials.permission.R
import com.ivianuu.essentials.permission.writesecuresettings.WriteSecureSettingsAction.GrantPermissionsViaRoot
import com.ivianuu.essentials.permission.writesecuresettings.WriteSecureSettingsAction.OpenPcInstructions
import com.ivianuu.essentials.shell.RunShellCommandUseCase
import com.ivianuu.essentials.store.StoreBuilder
import com.ivianuu.essentials.store.action
import com.ivianuu.essentials.ui.core.localVerticalInsetsPadding
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.KeyUiGivenScope
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.ui.navigation.StoreKeyUi
import com.ivianuu.essentials.util.BuildInfo
import com.ivianuu.essentials.util.StringResourceProvider
import com.ivianuu.essentials.util.Toaster
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.common.TypeKey
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.take
import kotlin.time.milliseconds

class WriteSecureSettingsKey(
    val permissionKey: TypeKey<WriteSecureSettingsPermission>
) : Key<Boolean>

@Given
val writeSecureSettingsUi: StoreKeyUi<WriteSecureSettingsKey, Unit,
        WriteSecureSettingsAction> = {
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
                    onClick = { send(OpenPcInstructions) }
                )
            }
            item {
                ListItem(
                    title = { Text(stringResource(R.string.es_pref_use_root)) },
                    subtitle = { Text(stringResource(R.string.es_pref_use_root_summary)) },
                    onClick = { send(GrantPermissionsViaRoot) }
                )
            }
        }
    }
}

sealed class WriteSecureSettingsAction {
    object OpenPcInstructions : WriteSecureSettingsAction()
    object GrantPermissionsViaRoot : WriteSecureSettingsAction()
}

@Given
fun writeSecureSettingsStore(
    @Given buildInfo: BuildInfo,
    @Given key: WriteSecureSettingsKey,
    @Given navigator: Navigator,
    @Given permissionStateFactory: PermissionStateFactory,
    @Given runShellCommand: RunShellCommandUseCase,
    @Given stringResource: StringResourceProvider,
    @Given toaster: Toaster,
): StoreBuilder<KeyUiGivenScope, Unit, WriteSecureSettingsAction> = {
    timer(200.milliseconds)
        .flatMapLatest { permissionStateFactory(listOf(key.permissionKey)) }
        .filter { it }
        .take(1)
        .onEach {
            toaster(stringResource(R.string.es_secure_settings_permission_granted, emptyList()))
            navigator.pop(key, true)
        }
        .launchIn(this)
    action<OpenPcInstructions> {
        navigator.push(WriteSecureSettingsPcInstructionsKey(key.permissionKey))
    }
    action<GrantPermissionsViaRoot> {
        runShellCommand(listOf("pm grant ${buildInfo.packageName} android.permission.WRITE_SECURE_SETTINGS"))
            .onFailure {
                it.printStackTrace()
                toaster(stringResource(R.string.es_secure_settings_no_root, emptyList()))
            }
    }
}
