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
import com.ivianuu.essentials.android.settings.*
import com.ivianuu.essentials.data.*
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.*
import com.ivianuu.essentials.store.*
import com.ivianuu.essentials.util.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.common.*
import kotlinx.coroutines.flow.*

@Given
object AutoRotationActionId : ActionId("auto_rotation")

@Given
fun autoRotationAction(
    @Given autoRotationIcon: Flow<AutoRotationIcon>,
    @Given stringResource: StringResourceProvider,
) = Action<AutoRotationActionId>(
    id = AutoRotationActionId,
    title = stringResource(R.string.es_action_auto_rotation, emptyList()),
    permissions = listOf(typeKeyOf<ActionWriteSettingsPermission>()),
    unlockScreen = true,
    icon = autoRotationIcon
)

@Given
fun autoRotationActionExecutor(
    @Given rotationSetting: DataStore<AutoRotation>,
): ActionExecutor<AutoRotationActionId> = {
    rotationSetting.updateData { if (this != 1) 1 else 0 }
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
