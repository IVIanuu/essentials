/*
 * Copyright 2018 Manuel Wrage
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

import android.os.Build
import com.ivianuu.injekt.annotations.Factory
import com.ivianuu.ksettings.KSettings
import com.ivianuu.ksettings.Setting
import com.ivianuu.timberktx.d

/**
 * Deactivates non sdk interface detection
 */
@Factory
class NonSdkInterfacesHelper(private val settings: KSettings) {

    fun disableNonSdkInterfaceDetection() {
        // todo use sdk ints once android q increases it
        if (Build.VERSION.CODENAME == "Q") {
            d { "disable non sdk on 29" }

            val hiddenApiPolicy = settings.int(
                "hidden_api_policy", Setting.Type.GLOBAL
            )
            hiddenApiPolicy.set(1)
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            d { "disable non sdk on p" }

            val hiddenApiPrePieAppsSetting = settings.int(
                "hidden_api_policy_pre_p_apps", Setting.Type.GLOBAL
            )
            val hiddenApiOnPieAppsSetting = settings.int(
                "hidden_api_policy_p_apps", Setting.Type.GLOBAL
            )

            hiddenApiPrePieAppsSetting.set(1)
            hiddenApiOnPieAppsSetting.set(1)
        }
    }

}