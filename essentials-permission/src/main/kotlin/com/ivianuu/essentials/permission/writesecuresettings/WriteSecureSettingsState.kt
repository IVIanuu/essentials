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

import com.ivianuu.essentials.permission.PermissionStateFactory
import com.ivianuu.essentials.permission.R
import com.ivianuu.essentials.permission.writesecuresettings.WriteSecureSettingsAction.GrantPermissionsViaRoot
import com.ivianuu.essentials.permission.writesecuresettings.WriteSecureSettingsAction.OpenPcInstructions
import com.ivianuu.essentials.result.onFailure
import com.ivianuu.essentials.result.runKatching
import com.ivianuu.essentials.shell.Shell
import com.ivianuu.essentials.store.Actions
import com.ivianuu.essentials.store.DispatchAction
import com.ivianuu.essentials.store.Initial
import com.ivianuu.essentials.store.state
import com.ivianuu.essentials.ui.navigation.NavigationAction
import com.ivianuu.essentials.ui.navigation.NavigationAction.Push
import com.ivianuu.essentials.ui.navigation.popWithResult
import com.ivianuu.essentials.ui.store.UiStateBinding
import com.ivianuu.essentials.util.BuildInfo
import com.ivianuu.essentials.util.Toaster
import com.ivianuu.injekt.Given
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

@UiStateBinding
@Given
fun writeSecureSettingsState(
    @Given scope: CoroutineScope,
    @Given initial: @Initial WriteSecureSettingsState = WriteSecureSettingsState,
    @Given actions: Actions<WriteSecureSettingsAction>,
    @Given buildInfo: BuildInfo,
    @Given key: WriteSecureSettingsKey,
    @Given navigator: DispatchAction<NavigationAction>,
    @Given permissionStateFactory: PermissionStateFactory,
    @Given shell: Shell,
    @Given toaster: Toaster
): StateFlow<WriteSecureSettingsState> = scope.state(initial) {
    launch {
        val state = permissionStateFactory(listOf(key.permissionKey))
        while (coroutineContext.isActive) {
            if (state.first()) {
                toaster.showToast(R.string.es_secure_settings_permission_granted)
                navigator.popWithResult(true)
                break
            }
            delay(200)
        }
    }
    actions
        .onEach { action ->
            when (action) {
                OpenPcInstructions -> navigator(
                    Push(WriteSecureSettingsPcInstructionsKey(key.permissionKey))
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
