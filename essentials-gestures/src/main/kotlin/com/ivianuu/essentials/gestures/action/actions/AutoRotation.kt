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
import com.ivianuu.essentials.android.settings.AndroidSettingStateModule
import com.ivianuu.essentials.android.settings.AndroidSettingsType
import com.ivianuu.essentials.android.settings.updateAndroidSetting
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.*
import com.ivianuu.essentials.store.Initial
import com.ivianuu.essentials.ui.core.Icon
import com.ivianuu.essentials.util.stringResource
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.GivenFun
import com.ivianuu.injekt.Module
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Given object AutoRotationActionId : ActionId("auto_rotation")

@ActionBinding<AutoRotationActionId>
@Given
fun autoRotationAction(
    @Given autoRotationIcon: Flow<AutoRotationIcon>,
    @Given choosePermissions: choosePermissions,
    @Given stringResource: stringResource,
): Action = Action(
    id = AutoRotationActionId,
    title = stringResource(R.string.es_action_auto_rotation),
    permissions = choosePermissions { listOf(writeSettings) },
    unlockScreen = true,
    icon = autoRotationIcon
)

@ActionExecutorBinding<AutoRotationActionId>
@GivenFun
suspend fun toggleAutoRotation(
    @Given updateAutoRotation: updateAndroidSetting<AutoRotation>,
) {
    updateAutoRotation { if (this != 1) 1 else 0 }
}

internal typealias AutoRotationIcon = ActionIcon

@Given
fun autoRotationIcon(@Given autoRotation: Flow<AutoRotation>): Flow<AutoRotationIcon> = autoRotation
    .map { it == 1 }
    .map {
        if (it) R.drawable.es_ic_screen_rotation
        else R.drawable.es_ic_screen_lock_rotation
    }
    .map { { Icon(it, null) } }

internal typealias AutoRotation = Int

@Module val autoRotationModule =
    AndroidSettingStateModule<AutoRotation, Int>(
        Settings.System.ACCELEROMETER_ROTATION, AndroidSettingsType.SYSTEM)

@Given val defaultAutoRotation: @Initial AutoRotation = 1
