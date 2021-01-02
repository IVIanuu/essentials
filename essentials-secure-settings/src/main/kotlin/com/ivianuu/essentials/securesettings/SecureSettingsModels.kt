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

import com.ivianuu.essentials.store.Initial
import com.ivianuu.injekt.Given

data class SecureSettingsKey(val showHideNavBarHint: Boolean = false)

data class SecureSettingsState(val showHideNavBarHint: Boolean = false) {
    companion object {
        @Given
        fun initial(@Given key: SecureSettingsKey): @Initial SecureSettingsState =
            SecureSettingsState(key.showHideNavBarHint)
    }
}

sealed class SecureSettingsAction {
    object GrantPermissionsViaRoot : SecureSettingsAction()
    object OpenPcInstructions : SecureSettingsAction()
}
