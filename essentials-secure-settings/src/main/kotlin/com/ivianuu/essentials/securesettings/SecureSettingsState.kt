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

package com.ivianuu.essentials.securesettings

import com.ivianuu.essentials.securesettings.SecureSettingsAction.GrantPermissionsViaRoot
import com.ivianuu.essentials.securesettings.SecureSettingsAction.OpenPcInstructions
import com.ivianuu.essentials.store.Actions
import com.ivianuu.essentials.store.DispatchAction
import com.ivianuu.essentials.store.Initial
import com.ivianuu.essentials.store.state
import com.ivianuu.essentials.ui.navigation.NavigationAction
import com.ivianuu.essentials.ui.store.UiStateBinding
import com.ivianuu.essentials.util.Toaster
import com.ivianuu.injekt.Given
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@UiStateBinding
@Given
fun secureSettingsState(
    @Given scope: CoroutineScope,
    @Given initial: @Initial SecureSettingsState = SecureSettingsState(),
    @Given actions: Actions<SecureSettingsAction>,
    @Given navigator: DispatchAction<NavigationAction>,
    @Given permission: SecureSettingsPermission,
    @Given toaster: Toaster,
): StateFlow<SecureSettingsState> = scope.state(initial) {
    launch { navigator.popOnceSecureSettingsPermissionIsGranted(true, permission, toaster) }
    actions
        .onEach { action ->
            when (action) {
                OpenPcInstructions -> navigator(
                    NavigationAction.Push(SecureSettingsPcInstructionsKey())
                )
                GrantPermissionsViaRoot -> if (permission.grantViaRoot()) {
                    toaster.showToast(R.string.es_secure_settings_permission_granted)
                } else {
                    toaster.showToast(R.string.es_secure_settings_no_root)
                }
            }
        }
        .launchIn(this)
}
