/*
 * Copyright 2019 Manuel Wrage
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

package com.ivianuu.essentials.hidenavbar

import com.github.ajalt.timberkt.d
import com.ivianuu.essentials.store.settings.SettingBox
import com.ivianuu.essentials.store.settings.SettingsBoxFactory
import com.ivianuu.essentials.store.settings.int
import com.ivianuu.essentials.util.SystemBuildInfo
import com.ivianuu.injekt.Factory

/**
 * Deactivates non sdk interface detection
 */
@Factory
internal class NonSdkInterfacesHelper(
    private val settings: SettingsBoxFactory,
    private val systemBuildInfo: SystemBuildInfo
) {

    suspend fun disableNonSdkInterfaceDetection() {
        if (systemBuildInfo.sdk >= 29) {
            d { "disable non sdk on 29" }

            val hiddenApiPolicy = settings.int(
                "hidden_api_policy", SettingBox.Type.Global
            )
            hiddenApiPolicy.set(1)
            hiddenApiPolicy.get()
        } else if (systemBuildInfo.sdk >= 28) {
            d { "disable non sdk on p" }

            val hiddenApiPrePieAppsSetting = settings.int(
                "hidden_api_policy_pre_p_apps",
                SettingBox.Type.Global
            )
            val hiddenApiOnPieAppsSetting = settings.int(
                "hidden_api_policy_p_apps",
                SettingBox.Type.Global
            )

            hiddenApiPrePieAppsSetting.set(1)
            hiddenApiOnPieAppsSetting.set(1)
        }
    }
}
