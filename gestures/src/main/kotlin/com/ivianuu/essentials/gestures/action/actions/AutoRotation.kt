/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.gestures.action.actions

import android.provider.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.res.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.compose.*
import com.ivianuu.essentials.data.*
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.common.*

@Provide object AutoRotationActionId : ActionId("auto_rotation")

@Provide fun autoRotationAction(
  autoRotationDataStore: DataStore<AutoRotation>,
  resources: Resources
) = Action(
  id = AutoRotationActionId,
  title = resources(R.string.action_auto_rotation),
  permissions = listOf(typeKeyOf<ActionWriteSettingsPermission>()),
  icon = {
    Icon(
      painterResource(
        if (autoRotationDataStore.data.collect(1) == 1) R.drawable.ic_screen_rotation
        else R.drawable.ic_screen_lock_rotation
      ),
      null
    )
  }
)

@Provide fun autoRotationActionExecutor(
  autoRotationDataStore: DataStore<AutoRotation>,
) = ActionExecutor<AutoRotationActionId> {
  autoRotationDataStore.updateData { if (this != 1) 1 else 0 }
}

@Tag annotation class AutoRotationTag
internal typealias AutoRotation = @AutoRotationTag Int

@Provide val autoRotationModule = AndroidSettingModule<AutoRotation, Int>(
  Settings.System.ACCELEROMETER_ROTATION, AndroidSettingsType.SYSTEM, 1
)
