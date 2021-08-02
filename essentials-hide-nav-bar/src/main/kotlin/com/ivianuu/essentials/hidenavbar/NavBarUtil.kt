/*
 * Copyright 2021 Manuel Wrage
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

import android.annotation.*
import android.graphics.*
import android.os.*
import android.view.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.android.settings.*
import com.ivianuu.essentials.data.*
import com.ivianuu.essentials.logging.*
import com.ivianuu.injekt.*

typealias NonSdkInterfaceDetectionDisabler = suspend () -> Unit

@Provide fun nonSdkInterfaceDetectionDisabler(
  systemBuildInfo: SystemBuildInfo,
  hiddenApiPolicyStore: DataStore<HiddenApiPolicy>,
  hiddenApiPolicyPrePieAppsStore: DataStore<HiddenApiPolicyPieApps>,
  hiddenApiPolicyPieAppsStore: DataStore<HiddenApiPolicyPieApps>,
  logger: Logger,
): NonSdkInterfaceDetectionDisabler = {
  if (systemBuildInfo.sdk >= 29) {
    d { "disable non sdk on 29" }
    hiddenApiPolicyStore.updateData { 1 }
    d { "disabled non sdk on 29" }
  } else if (systemBuildInfo.sdk >= 28) {
    d { "disable non sdk on p" }
    hiddenApiPolicyPrePieAppsStore.updateData { 1 }
    hiddenApiPolicyPieAppsStore.updateData { 1 }
    d { "disabled non sdk on p" }
  }
}

internal typealias HiddenApiPolicy = Int

@Provide val hiddenApiPolicyModule =
  AndroidSettingModule<HiddenApiPolicy, Int>("hidden_api_policy", AndroidSettingsType.GLOBAL, 0)

internal typealias HiddenApiPolicyPrePieApps = Int

@Provide val hiddenApiPolicyPrePieAppsModule =
  AndroidSettingModule<HiddenApiPolicyPrePieApps, Int>(
    "hidden_api_policy_pre_p_apps",
    AndroidSettingsType.GLOBAL,
    0
  )

internal typealias HiddenApiPolicyPieApps = Int

@Provide val hiddenApiPolicyPieAppsModule = AndroidSettingModule<HiddenApiPolicyPieApps, Int>(
  "hidden_api_policy_p_apps",
  AndroidSettingsType.GLOBAL,
  0
)

typealias OverscanUpdater = (Rect) -> Unit

@SuppressLint("PrivateApi")
@Provide
fun overscanUpdater(logger: Logger): OverscanUpdater = { rect ->
  d { "set overscan $rect" }

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
