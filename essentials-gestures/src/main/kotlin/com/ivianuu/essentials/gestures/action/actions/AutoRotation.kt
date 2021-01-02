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

import android.provider.Settings
import com.ivianuu.essentials.android.settings.AndroidSettingsType
import com.ivianuu.essentials.android.settings.androidSettingStateBinding
import com.ivianuu.essentials.android.settings.updateAndroidSetting
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.gestures.action.ActionBinding
import com.ivianuu.essentials.gestures.action.ActionExecutorBinding
import com.ivianuu.essentials.gestures.action.ActionIcon
import com.ivianuu.essentials.gestures.action.choosePermissions
import com.ivianuu.essentials.store.Initial
import com.ivianuu.essentials.ui.core.Icon
import com.ivianuu.essentials.util.stringResource
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.GivenFun
import com.ivianuu.injekt.Module
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

//@ActionBinding("auto_rotation")
fun autoRotationAction(
    autoRotationIcon: Flow<AutoRotationIcon>,
    choosePermissions: choosePermissions,
    stringResource: stringResource,
): Action = Action(
    id = "auto_rotation",
    title = stringResource(R.string.es_action_auto_rotation),
    permissions = choosePermissions { listOf(writeSettings) },
    unlockScreen = true,
    icon = autoRotationIcon
)

//@ActionExecutorBinding("auto_rotation")
@GivenFun suspend fun toggleAutoRotation(
    updateAutoRotation: updateAndroidSetting<AutoRotation>,
) {
    updateAutoRotation { if (this != 1) 1 else 0 }
}

internal typealias AutoRotationIcon = ActionIcon

@Given
fun AutoRotationIcon(@Given autoRotation: Flow<AutoRotation>): Flow<AutoRotationIcon> = autoRotation
    .map { it == 1 }
    .map {
        if (it) R.drawable.es_ic_screen_rotation
        else R.drawable.es_ic_screen_lock_rotation
    }
    .map { { Icon(it) } }

internal typealias AutoRotation = Int

@Module val autoRotationModule =
    androidSettingStateBinding<AutoRotation>(
        Settings.System.ACCELEROMETER_ROTATION, AndroidSettingsType.SYSTEM)

@Given val defaultAutoRotation: @Initial AutoRotation = 1
