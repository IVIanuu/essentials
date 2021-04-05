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
import androidx.compose.material.Icon
import androidx.compose.ui.res.painterResource
import com.ivianuu.essentials.android.settings.AndroidSetting
import com.ivianuu.essentials.android.settings.AndroidSettingModule
import com.ivianuu.essentials.android.settings.AndroidSettingsType
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.gestures.action.ActionBinding
import com.ivianuu.essentials.gestures.action.ActionExecutor
import com.ivianuu.essentials.gestures.action.ActionExecutorBinding
import com.ivianuu.essentials.gestures.action.ActionIcon
import com.ivianuu.essentials.gestures.action.ActionId
import com.ivianuu.essentials.gestures.action.ActionWriteSettingsPermission
import com.ivianuu.essentials.store.Initial
import com.ivianuu.essentials.util.ResourceProvider
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.common.typeKeyOf
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Given
object AutoRotationActionId : ActionId("auto_rotation")

@Given
fun autoRotationAction(
    @Given autoRotationIcon: Flow<AutoRotationIcon>,
    @Given resourceProvider: ResourceProvider,
): @ActionBinding<AutoRotationActionId> Action = Action(
    id = AutoRotationActionId,
    title = resourceProvider.string(R.string.es_action_auto_rotation),
    permissions = listOf(typeKeyOf<ActionWriteSettingsPermission>()),
    unlockScreen = true,
    icon = autoRotationIcon
)

@Given
fun autoRotationActionExecutor(
    @Given rotationSetting: AndroidSetting<AutoRotation>,
): @ActionExecutorBinding<AutoRotationActionId> ActionExecutor = {
    rotationSetting.update { if (this != 1) 1 else 0 }
}

internal typealias AutoRotationIcon = ActionIcon

@Given
fun autoRotationIcon(@Given autoRotation: Flow<AutoRotation>): Flow<AutoRotationIcon> = autoRotation
    .map { it == 1 }
    .map {
        if (it) R.drawable.es_ic_screen_rotation
        else R.drawable.es_ic_screen_lock_rotation
    }
    .map { { Icon(painterResource(it), null) } }

internal typealias AutoRotation = Int

@Given
val autoRotationModule = AndroidSettingModule<AutoRotation, Int>(
    Settings.System.ACCELEROMETER_ROTATION, AndroidSettingsType.SYSTEM)

@Given
val defaultAutoRotation: @Initial AutoRotation = 1
