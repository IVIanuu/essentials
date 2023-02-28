/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.hidenavbar

import android.annotation.SuppressLint
import android.graphics.Rect
import android.os.IBinder
import android.view.Display
import com.ivianuu.essentials.SystemBuildInfo
import com.ivianuu.essentials.android.settings.AndroidSettingModule
import com.ivianuu.essentials.android.settings.AndroidSettingsType
import com.ivianuu.essentials.data.DataStore
import com.ivianuu.essentials.logging.Logger
import com.ivianuu.essentials.logging.log
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.Tag

fun interface NonSdkInterfaceDetectionDisabler {
  suspend operator fun invoke()
}

@Provide fun nonSdkInterfaceDetectionDisabler(
  logger: Logger,
  systemBuildInfo: SystemBuildInfo,
  hiddenApiPolicyStore: DataStore<HiddenApiPolicy>,
  hiddenApiPolicyPrePieAppsStore: DataStore<HiddenApiPolicyPieApps>,
  hiddenApiPolicyPieAppsStore: DataStore<HiddenApiPolicyPieApps>
) = NonSdkInterfaceDetectionDisabler {
  if (systemBuildInfo.sdk >= 29) {
    logger.log { "disable non sdk on 29" }
    hiddenApiPolicyStore.updateData { 1 }
    logger.log { "disabled non sdk on 29" }
  } else if (systemBuildInfo.sdk >= 28) {
    logger.log { "disable non sdk on p" }
    hiddenApiPolicyPrePieAppsStore.updateData { 1 }
    hiddenApiPolicyPieAppsStore.updateData { 1 }
    logger.log { "disabled non sdk on p" }
  }
}

@Tag internal annotation class HiddenApiPolicyTag
internal typealias HiddenApiPolicy = @HiddenApiPolicyTag Int

@Provide val hiddenApiPolicyModule =
  AndroidSettingModule<HiddenApiPolicy, Int>("hidden_api_policy", AndroidSettingsType.GLOBAL, 0)

@Tag internal annotation class HiddenApiPolicyPrePieAppsTag
internal typealias HiddenApiPolicyPrePieApps = @HiddenApiPolicyPrePieAppsTag Int

@Provide val hiddenApiPolicyPrePieAppsModule =
  AndroidSettingModule<HiddenApiPolicyPrePieApps, Int>(
    "hidden_api_policy_pre_p_apps",
    AndroidSettingsType.GLOBAL,
    0
  )

@Tag internal annotation class HiddenApiPolicyPieAppsTag
internal typealias HiddenApiPolicyPieApps = @HiddenApiPolicyPieAppsTag Int

@Provide val hiddenApiPolicyPieAppsModule = AndroidSettingModule<HiddenApiPolicyPieApps, Int>(
  "hidden_api_policy_p_apps",
  AndroidSettingsType.GLOBAL,
  0
)

fun interface OverscanUpdater {
  suspend operator fun invoke(rect: Rect)
}

@SuppressLint("PrivateApi") @Provide fun overscanUpdater(logger: Logger) = OverscanUpdater { rect ->
  logger.log { "set overscan $rect" }

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
