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

package com.ivianuu.essentials.gestures.action.actions

import android.provider.Settings
import androidx.compose.material.Icon
import com.ivianuu.essentials.Res
import com.ivianuu.essentials.android.settings.AndroidSettingModule
import com.ivianuu.essentials.android.settings.AndroidSettingsType
import com.ivianuu.essentials.data.DataStore
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.gestures.action.ActionExecutor
import com.ivianuu.essentials.gestures.action.ActionIcon
import com.ivianuu.essentials.gestures.action.ActionId
import com.ivianuu.essentials.gestures.action.ActionWriteSettingsPermission
import com.ivianuu.essentials.loadResource
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.Tag
import com.ivianuu.injekt.common.typeKeyOf
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Provide object AutoRotationActionId : ActionId("auto_rotation")

@Provide @Res fun autoRotationAction(autoRotation: DataStore<AutoRotation>) = Action(
  id = AutoRotationActionId,
  title = loadResource(R.string.es_action_auto_rotation),
  permissions = listOf(typeKeyOf<ActionWriteSettingsPermission>()),
  icon = autoRotation.data.autoRotationIcon()
)

@Provide fun autoRotationActionExecutor(
  rotationSetting: DataStore<AutoRotation>,
): ActionExecutor<AutoRotationActionId> = {
  rotationSetting.updateData { if (this != 1) 1 else 0 }
}

private fun Flow<AutoRotation>.autoRotationIcon(): Flow<ActionIcon> =
  this
    .map { it == 1 }
    .map {
      if (it) R.drawable.es_ic_screen_rotation
      else R.drawable.es_ic_screen_lock_rotation
    }
    .map { { Icon(it) } }

@Tag annotation class AutoRotationTag
internal typealias AutoRotation = @AutoRotationTag Int

@Provide val autoRotationModule = AndroidSettingModule<AutoRotation, Int>(
  Settings.System.ACCELEROMETER_ROTATION, AndroidSettingsType.SYSTEM, 1
)
