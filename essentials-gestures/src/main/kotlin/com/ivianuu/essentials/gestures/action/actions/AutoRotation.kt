/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.gestures.action.actions

import android.provider.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.android.settings.*
import com.ivianuu.essentials.data.*
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.common.*
import kotlinx.coroutines.flow.*

@Provide object AutoRotationActionId : ActionId("auto_rotation")

@Provide fun autoRotationAction(
  autoRotation: DataStore<AutoRotation>,
  RP: ResourceProvider
) = Action(
  id = AutoRotationActionId,
  title = loadResource(R.string.es_action_auto_rotation),
  permissions = listOf(typeKeyOf<ActionWriteSettingsPermission>()),
  icon = autoRotation.data.autoRotationIcon()
)

@Provide fun autoRotationActionExecutor(
  rotationSetting: DataStore<AutoRotation>,
) = ActionExecutor<AutoRotationActionId> {
  rotationSetting.updateData { if (this != 1) 1 else 0 }
}

private fun Flow<AutoRotation>.autoRotationIcon() = ActionIcon {
  val enabled = collectAsState(1).value == 1
  Icon(
    if (enabled) R.drawable.es_ic_screen_rotation
    else R.drawable.es_ic_screen_lock_rotation
  )
}

@Tag annotation class AutoRotationTag
internal typealias AutoRotation = @AutoRotationTag Int

@Provide val autoRotationModule = AndroidSettingModule<AutoRotation, Int>(
  Settings.System.ACCELEROMETER_ROTATION, AndroidSettingsType.SYSTEM, 1
)
