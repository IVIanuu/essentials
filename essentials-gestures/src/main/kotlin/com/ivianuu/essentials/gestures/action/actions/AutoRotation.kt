/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.gestures.action.actions

import android.provider.Settings
import androidx.compose.material3.Icon
import androidx.compose.runtime.collectAsState
import com.ivianuu.essentials.Resources
import com.ivianuu.essentials.android.settings.AndroidSettingModule
import com.ivianuu.essentials.android.settings.AndroidSettingsType
import com.ivianuu.essentials.data.DataStore
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.gestures.action.ActionExecutor
import com.ivianuu.essentials.gestures.action.ActionIcon
import com.ivianuu.essentials.gestures.action.ActionId
import com.ivianuu.essentials.gestures.action.ActionWriteSettingsPermission
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.Tag
import com.ivianuu.injekt.common.typeKeyOf
import kotlinx.coroutines.flow.Flow

@Provide object AutoRotationActionId : ActionId("auto_rotation")

@Provide fun autoRotationAction(
  autoRotation: DataStore<AutoRotation>,
  resources: Resources
) = Action(
  id = AutoRotationActionId,
  title = resources(R.string.es_action_auto_rotation),
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
