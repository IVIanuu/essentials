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

import androidx.compose.foundation.lazy.*
import androidx.compose.material.Text
import androidx.compose.ui.res.*
import com.github.michaelbull.result.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.coroutines.*
import com.ivianuu.essentials.optics.*
import com.ivianuu.essentials.permission.*
import com.ivianuu.essentials.permission.R
import com.ivianuu.essentials.shell.*
import com.ivianuu.essentials.store.*
import com.ivianuu.essentials.ui.core.*
import com.ivianuu.essentials.ui.material.*
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.essentials.util.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.common.*
import com.ivianuu.injekt.scope.*
import kotlinx.coroutines.flow.*
import kotlin.time.*

data class WriteSecureSettingsKey(
    val permissionKey: TypeKey<WriteSecureSettingsPermission>
) : Key<Boolean>

@Given
val writeSecureSettingsUi: ModelKeyUi<WriteSecureSettingsKey, WriteSecureSettingsModel> = {
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
                    onClick = model.openPcInstructions
                )
            }
            item {
                ListItem(
                    title = { Text(stringResource(R.string.es_pref_use_root)) },
                    subtitle = { Text(stringResource(R.string.es_pref_use_root_summary)) },
                    onClick = model.grantPermissionsViaRoot
                )
            }
        }
    }
}

@Optics
data class WriteSecureSettingsModel(
    val openPcInstructions: () -> Unit = {},
    val grantPermissionsViaRoot: () -> Unit = {}
)

@Given
fun writeSecureSettingsModel(
    @Given buildInfo: BuildInfo,
    @Given key: WriteSecureSettingsKey,
    @Given navigator: Navigator,
    @Given permissionStateFactory: PermissionStateFactory,
    @Given runShellCommand: RunShellCommandUseCase,
    @Given scope: ScopeCoroutineScope<KeyUiGivenScope>,
    @Given stringResource: StringResourceProvider,
    @Given toaster: Toaster,
): @Scoped<KeyUiGivenScope> StateFlow<WriteSecureSettingsModel> = scope.state(
    WriteSecureSettingsModel()
) {
    timer(200.milliseconds)
        .flatMapLatest { permissionStateFactory(listOf(key.permissionKey)) }
        .filter { it }
        .take(1)
        .onEach {
            toaster(stringResource(R.string.es_secure_settings_permission_granted, emptyList()))
            navigator.pop(key, true)
        }
        .launchIn(this)
    action(WriteSecureSettingsModel.openPcInstructions()) {
        navigator.push(WriteSecureSettingsPcInstructionsKey(key.permissionKey))
    }
    action(WriteSecureSettingsModel.grantPermissionsViaRoot()) {
        runShellCommand(listOf("pm grant ${buildInfo.packageName} android.permission.WRITE_SECURE_SETTINGS"))
            .onFailure {
                it.printStackTrace()
                toaster(stringResource(R.string.es_secure_settings_no_root, emptyList()))
            }
    }
}
