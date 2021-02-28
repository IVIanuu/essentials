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

import com.ivianuu.essentials.store.Initial
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.util.BuildInfo
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.common.TypeKey

data class WriteSecureSettingsPcInstructionsKey(
    val permissionKey: TypeKey<WriteSecureSettingsPermission>
) : Key<Nothing>

data class WriteSecureSettingsPcInstructionsState(val packageName: String) {
    val secureSettingsAdbCommand =
        "adb shell pm grant $packageName android.permission.WRITE_SECURE_SETTINGS"
}

@Given
fun initialWriteSecureSettingsPcInstructionsState(@Given buildInfo: BuildInfo):
        @Initial WriteSecureSettingsPcInstructionsState =
    WriteSecureSettingsPcInstructionsState(packageName = buildInfo.packageName)

sealed class WriteSecureSettingsPcInstructionsAction {
    object CopyAdbCommand : WriteSecureSettingsPcInstructionsAction()
    object OpenGadgetHacksTutorial : WriteSecureSettingsPcInstructionsAction()
    object OpenLifeHackerTutorial : WriteSecureSettingsPcInstructionsAction()
    object OpenXdaTutorial : WriteSecureSettingsPcInstructionsAction()
}
