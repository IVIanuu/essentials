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
import com.ivianuu.essentials.datastore.DataStore
import com.ivianuu.essentials.datastore.android.settings.SettingDataStore
import com.ivianuu.essentials.datastore.android.settings.SettingsDataStoreFactory
import com.ivianuu.essentials.datastore.android.settings.int
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.gestures.action.ActionBinding
import com.ivianuu.essentials.gestures.action.ActionExecutorBinding
import com.ivianuu.essentials.gestures.action.ActionIcon
import com.ivianuu.essentials.gestures.action.choosePermissions
import com.ivianuu.essentials.ui.core.Icon
import com.ivianuu.essentials.util.stringResource
import com.ivianuu.injekt.Binding
import com.ivianuu.injekt.FunBinding
import com.ivianuu.injekt.merge.ApplicationComponent
import kotlinx.coroutines.flow.map

@ActionBinding("auto_rotation")
fun autoRotationAction(
    autoRotationIcon: AutoRotationIcon,
    choosePermissions: choosePermissions,
    stringResource: stringResource,
): Action = Action(
    key = "auto_rotation",
    title = stringResource(R.string.es_action_auto_rotation),
    permissions = choosePermissions { listOf(writeSettings) },
    unlockScreen = true,
    icon = autoRotationIcon
)

@ActionExecutorBinding("auto_rotation")
@FunBinding
suspend fun toggleAutoRotation(setting: AutoRotationSetting) {
    setting.updateData { if (this != 1) 1 else 0 }
}

@Binding(ApplicationComponent::class)
fun autoRotationSetting(factory: SettingsDataStoreFactory): AutoRotationSetting = factory
    .int(Settings.System.ACCELEROMETER_ROTATION, SettingDataStore.Type.System, 1)

typealias AutoRotationIcon = ActionIcon

@Binding
fun AutoRotationIcon(setting: AutoRotationSetting): AutoRotationIcon = setting.data
    .map { it == 1 }
    .map {
        if (it) R.drawable.es_ic_screen_rotation
        else R.drawable.es_ic_screen_lock_rotation
    }
    .map { { Icon(it) } }

typealias AutoRotationSetting = DataStore<Int>
