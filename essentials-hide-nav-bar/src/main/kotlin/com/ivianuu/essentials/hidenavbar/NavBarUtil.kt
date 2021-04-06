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

import android.annotation.SuppressLint
import android.graphics.Rect
import android.os.IBinder
import android.view.Display
import com.ivianuu.essentials.android.settings.AndroidSettingStoreModule
import com.ivianuu.essentials.android.settings.AndroidSettingsType
import com.ivianuu.essentials.data.ValueAction
import com.ivianuu.essentials.data.updateAndAwait
import com.ivianuu.essentials.store.Initial
import com.ivianuu.essentials.store.Store
import com.ivianuu.essentials.util.Logger
import com.ivianuu.essentials.util.SystemBuildInfo
import com.ivianuu.essentials.util.d
import com.ivianuu.injekt.Given

typealias NonSdkInterfaceDetectionDisabler = suspend () -> Unit

@Given
fun nonSdkInterfaceDetectionDisabler(
    @Given logger: Logger,
    @Given systemBuildInfo: SystemBuildInfo,
    @Given hiddenApiPolicyStore: Store<HiddenApiPolicy, ValueAction<HiddenApiPolicy>>,
    @Given hiddenApiPolicyPrePieAppsStore: Store<HiddenApiPolicyPieApps, ValueAction<HiddenApiPolicyPieApps>>,
    @Given hiddenApiPolicyPieAppsStore: Store<HiddenApiPolicyPieApps, ValueAction<HiddenApiPolicyPieApps>>,
): NonSdkInterfaceDetectionDisabler = {
    if (systemBuildInfo.sdk >= 29) {
        logger.d { "disable non sdk on 29" }
        hiddenApiPolicyStore.updateAndAwait { 1 }
        logger.d { "disabled non sdk on 29" }
    } else if (systemBuildInfo.sdk >= 28) {
        logger.d { "disable non sdk on p" }
        hiddenApiPolicyPrePieAppsStore.updateAndAwait { 1 }
        hiddenApiPolicyPieAppsStore.updateAndAwait { 1 }
        logger.d { "disabled non sdk on p" }
    }
}

internal typealias HiddenApiPolicy = Int

@Given
val hiddenApiPolicyModule =
    AndroidSettingStoreModule<HiddenApiPolicy, Int>("hidden_api_policy", AndroidSettingsType.GLOBAL)

@Given
val defaultHiddenApiPolicy: @Initial HiddenApiPolicy = 0

internal typealias HiddenApiPolicyPrePieApps = Int

@Given
val hiddenApiPolicyPrePieAppsModule =
    AndroidSettingStoreModule<HiddenApiPolicyPrePieApps, Int>("hidden_api_policy_pre_p_apps",
        AndroidSettingsType.GLOBAL)

@Given
val defaultHiddenApiPolicyPrePieApps: @Initial HiddenApiPolicyPrePieApps = 0

internal typealias HiddenApiPolicyPieApps = Int

@Given
val hiddenApiPolicyPieAppsBinding =
    AndroidSettingStoreModule<HiddenApiPolicyPieApps, Int>("hidden_api_policy_p_apps",
        AndroidSettingsType.GLOBAL)

@Given
val defaultHiddenApiPolicyPieApps: @Initial HiddenApiPolicyPieApps = 0

typealias OverscanUpdater = (Rect) -> Unit

@SuppressLint("PrivateApi")
@Given
fun overscanUpdater(@Given logger: Logger): OverscanUpdater = { rect ->
    logger.d { "set overscan $rect" }

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
