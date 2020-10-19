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

package com.ivianuu.essentials.hidenavbar

import android.graphics.Rect
import android.os.IBinder
import android.view.Display
import com.ivianuu.essentials.datastore.android.settings.SettingDataStore
import com.ivianuu.essentials.datastore.android.settings.SettingsDataStoreFactory
import com.ivianuu.essentials.datastore.android.settings.int
import com.ivianuu.essentials.util.Logger
import com.ivianuu.injekt.Binding

typealias disableNonSdkInterfaceDetection = suspend () -> Unit
@Binding
fun disableNonSdkInterfaceDetection(
    logger: Logger,
    systemBuildInfo: com.ivianuu.essentials.util.SystemBuildInfo,
    settingsDataStoreFactory: SettingsDataStoreFactory,
): disableNonSdkInterfaceDetection = {
    if (systemBuildInfo.sdk >= 29) {
        logger.d("disable non sdk on 29")

        val hiddenApiPolicy = settingsDataStoreFactory.int(
            "hidden_api_policy", SettingDataStore.Type.Global
        )
        hiddenApiPolicy.updateData { 1 }
    } else if (systemBuildInfo.sdk >= 28) {
        logger.d("disable non sdk on p")

        val hiddenApiPrePieAppsSetting = settingsDataStoreFactory.int(
            "hidden_api_policy_pre_p_apps",
            SettingDataStore.Type.Global
        )
        val hiddenApiOnPieAppsSetting = settingsDataStoreFactory.int(
            "hidden_api_policy_p_apps",
            SettingDataStore.Type.Global
        )

        hiddenApiPrePieAppsSetting.updateData { 1 }
        hiddenApiOnPieAppsSetting.updateData { 1 }
    }
}

typealias setOverscan = (Rect) -> Unit
@Binding
fun setOverscan(logger: Logger): setOverscan = { rect ->
    logger.d("set overscan $rect")

    val cls = Class.forName("android.view.IWindowManager\$Stub")
    val invoke = Class.forName("android.os.ServiceManager")
        .getMethod("checkService", String::class.java)
        .invoke(null, "window")

    val windowManagerService = cls.getMethod("asInterface", IBinder::class.java)
        .invoke(null, invoke)!!

    val setOverscanMethod = windowManagerService.javaClass.getDeclaredMethod(
        "setOverscan",
        Int::class.java, Int::class.java, Int::class.java, Int::class.java, Int::class.java
    ).apply { isAccessible = true }

    setOverscanMethod.invoke(
        windowManagerService,
        Display.DEFAULT_DISPLAY, rect.left, rect.top, rect.right, rect.bottom
    )
}
