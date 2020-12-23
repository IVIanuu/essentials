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
import com.ivianuu.essentials.util.BuildInfo
import com.ivianuu.injekt.Given

class SecureSettingsPcInstructionsKey

data class SecureSettingsPcInstructionsState(val packageName: String) {
    val secureSettingsAdbCommand =
        "adb shell pm grant $packageName android.permission.WRITE_SECURE_SETTINGS"

    companion object {
        @Given
        fun initial(buildInfo: BuildInfo): @Initial SecureSettingsPcInstructionsState =
            SecureSettingsPcInstructionsState(packageName = buildInfo.packageName)
    }
}

sealed class SecureSettingsPcInstructionsAction {
    object CopyAdbCommand : SecureSettingsPcInstructionsAction()
    object OpenGadgetHacksTutorial : SecureSettingsPcInstructionsAction()
    object OpenLifeHackerTutorial : SecureSettingsPcInstructionsAction()
    object OpenXdaTutorial : SecureSettingsPcInstructionsAction()
}
