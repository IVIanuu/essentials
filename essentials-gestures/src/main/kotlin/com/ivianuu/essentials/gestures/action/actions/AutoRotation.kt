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

package com.ivianuu.essentials.gestures.action.actions

import android.provider.*
import androidx.compose.material.*
import androidx.compose.ui.res.*
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
  autoRotation: Flow<AutoRotation>,
  rp: ResourceProvider,
): Action<AutoRotationActionId> = Action(
  id = AutoRotationActionId,
  title = loadResource(R.string.es_action_auto_rotation),
  permissions = listOf(typeKeyOf<ActionWriteSettingsPermission>()),
  unlockScreen = true,
  icon = autoRotation.autoRotationIcon()
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
    .map { { Icon(painterResource(it), null) } }

internal typealias AutoRotation = Int

@Provide val autoRotationModule = AndroidSettingModule<AutoRotation, Int>(
  Settings.System.ACCELEROMETER_ROTATION, AndroidSettingsType.SYSTEM, 1
)
