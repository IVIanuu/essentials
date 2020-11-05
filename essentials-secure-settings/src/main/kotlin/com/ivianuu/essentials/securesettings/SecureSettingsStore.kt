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
import com.ivianuu.essentials.securesettings.SecureSettingsAction.ShowToPcInstructions
import com.ivianuu.essentials.store.iterator
import com.ivianuu.essentials.store.store
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.ui.navigation.push
import com.ivianuu.essentials.ui.store.StoreBinding
import com.ivianuu.essentials.util.exhaustive
import com.ivianuu.essentials.util.showToastRes
import com.ivianuu.injekt.Binding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@StoreBinding
fun CoroutineScope.SecureSettingsStore(
    grantSecureSettingsPermissionViaRoot: grantSecureSettingsPermissionViaRoot,
    navigator: Navigator,
    popNavigatorOnceSecureSettingsGranted: popNavigatorOnceSecureSettingsGranted,
    secureSettingsPcInstructionsPage: SecureSettingsPcInstructionsPage,
    showToastRes: showToastRes
) = store<SecureSettingsState, SecureSettingsAction>(
    SecureSettingsState
) {
    launch { popNavigatorOnceSecureSettingsGranted(true) }

    for (action in this) {
        when (action) {
            ShowToPcInstructions -> navigator.push { secureSettingsPcInstructionsPage() }
            GrantPermissionsViaRoot -> {
                if (grantSecureSettingsPermissionViaRoot()) {
                    showToastRes(R.string.es_secure_settings_permission_granted)
                } else {
                    showToastRes(R.string.es_secure_settings_no_root)
                }
            }
        }.exhaustive
    }
}
