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

package com.ivianuu.essentials.permission.writesecuresettings

import android.content.Intent
import android.os.BatteryManager
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ivianuu.essentials.BuildInfo
import com.ivianuu.essentials.ResourceProvider
import com.ivianuu.essentials.android.settings.AndroidSettingModule
import com.ivianuu.essentials.android.settings.AndroidSettingsType
import com.ivianuu.essentials.coroutines.race
import com.ivianuu.essentials.data.DataStore
import com.ivianuu.essentials.onFailure
import com.ivianuu.essentials.optics.Optics
import com.ivianuu.essentials.permission.PermissionStateFactory
import com.ivianuu.essentials.permission.R
import com.ivianuu.essentials.store.action
import com.ivianuu.essentials.store.state
import com.ivianuu.essentials.ui.common.SimpleListScreen
import com.ivianuu.essentials.ui.material.Button
import com.ivianuu.essentials.ui.material.OutlinedButton
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.KeyUiComponent
import com.ivianuu.essentials.ui.navigation.ModelKeyUi
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.ui.navigation.toIntentKey
import com.ivianuu.essentials.ui.stepper.Step
import com.ivianuu.essentials.util.AppUiStarter
import com.ivianuu.essentials.util.BroadcastsFactory
import com.ivianuu.essentials.util.Toaster
import com.ivianuu.essentials.util.showToast
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.Tag
import com.ivianuu.injekt.common.TypeKey
import com.ivianuu.injekt.coroutines.ComponentScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

data class WriteSecureSettingsPcInstructionsKey(
  val permissionKey: TypeKey<WriteSecureSettingsPermission>
) : Key<Boolean>

@Provide val writeSecureSettingsPcInstructionsUi: ModelKeyUi<WriteSecureSettingsPcInstructionsKey,
    WriteSecureSettingsPcInstructionsModel> = {
  SimpleListScreen(R.string.es_secure_settings_pc_instructions_title) {
    item {
      SecureSettingsHeader(
        text = stringResource(R.string.es_secure_settings_pc_instructions_desc)
      )
    }

    item {
      Step(
        step = 1,
        isCompleted = model.completedStep > 1,
        isCurrent = model.currentStep == 1,
        onClick = { model.openStep(1) },
        title = { Text(R.string.es_secure_settings_step_1_title) },
        content = {
          Text(
            textResId = R.string.es_secure_settings_step_1_content,
            style = MaterialTheme.typography.body2
          )
        },
        actions = {
          Button(
            onClick = model.continueStep,
            enabled = model.canContinueStep
          ) { Text(R.string.es_continue) }
          OutlinedButton(onClick = model.openPhoneInfo) { Text(R.string.open_phone_info) }
        }
      )
    }

    item {
      Step(
        step = 2,
        isCompleted = model.completedStep > 2,
        isCurrent = model.currentStep == 2,
        onClick = { model.openStep(2) },
        title = { Text(R.string.es_secure_settings_step_2_title) },
        content = {
          Text(
            textResId = R.string.es_secure_settings_step_2_content,
            style = MaterialTheme.typography.body2
          )
        },
        actions = {
          Button(
            onClick = model.continueStep,
            enabled = model.canContinueStep
          ) { Text(R.string.es_continue) }
          OutlinedButton(onClick = model.openDeveloperSettings) { Text(R.string.open_developer_settings) }
        }
      )
    }

    item {
      Step(
        step = 3,
        isCompleted = model.completedStep > 3,
        isCurrent = model.currentStep == 3,
        onClick = { model.openStep(3) },
        title = { Text(R.string.es_secure_settings_step_3_title) },
        content = { Text(R.string.es_secure_settings_step_3_content) },
        actions = {
          Button(
            onClick = model.continueStep,
            enabled = model.canContinueStep
          ) { Text(R.string.es_continue) }
        }
      )
    }

    item {
      Step(
        step = 4,
        isCompleted = model.completedStep > 4,
        isCurrent = model.currentStep == 4,
        onClick = { model.openStep(4) },
        title = { Text(R.string.es_secure_settings_step_4_title) },
        content = {
          Text(R.string.es_secure_settings_step_4_content)

          Text(
            modifier = Modifier
              .padding(top = 8.dp)
              .background(LocalContentColor.current.copy(alpha = 0.12f), RoundedCornerShape(4.dp))
              .padding(4.dp),
            text = model.adbCommand,
            style = MaterialTheme.typography.body2.copy(fontSize = 14.sp)
          )
        },
        actions = {
          Button(
            onClick = model.continueStep,
            enabled = model.canContinueStep
          ) { Text(R.string.es_complete) }
        }
      )
    }
  }
}

@Optics data class WriteSecureSettingsPcInstructionsModel(
  val currentStep: Int = 1,
  val completedStep: Int = 1,
  val canContinueStep: Boolean = false,
  val packageName: String = "",
  val openPhoneInfo: () -> Unit = {},
  val openDeveloperSettings: () -> Unit = {},
  val continueStep: () -> Unit = {},
  val openStep: (Int) -> Unit = {},
  val cancelStep: () -> Unit = {}
) {
  val adbCommand = "pm grant $packageName android.permission.WRITE_SECURE_SETTINGS"
}

@Tag annotation class DeveloperModeTag
typealias DeveloperMode = @DeveloperModeTag Int

@Provide val developerModeSetting = AndroidSettingModule<DeveloperMode, Int>(
  Settings.Global.DEVELOPMENT_SETTINGS_ENABLED,
  AndroidSettingsType.GLOBAL,
  0
)

@Tag annotation class AdbEnabledTag
typealias AdbEnabled = @AdbEnabledTag Int

@Provide val adbEnabledSetting = AndroidSettingModule<AdbEnabled, Int>(
  Settings.Global.ADB_ENABLED,
  AndroidSettingsType.GLOBAL,
  0
)

@JvmInline value class IsCharging(val value: Boolean)

@Provide fun isCharging(
  broadcastsFactory: BroadcastsFactory
): Flow<IsCharging> = broadcastsFactory(Intent.ACTION_BATTERY_CHANGED)
  .map {
    IsCharging(
      it.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ==
          BatteryManager.BATTERY_STATUS_CHARGING
    )
  }
  .distinctUntilChanged()

@Provide fun writeSecureSettingsPcInstructionsModel(
  adbEnabledSetting: DataStore<AdbEnabled>,
  appUiStarter: AppUiStarter,
  buildInfo: BuildInfo,
  developerModeSetting: DataStore<DeveloperMode>,
  isCharging: Flow<IsCharging>,
  key: WriteSecureSettingsPcInstructionsKey,
  navigator: Navigator,
  permissionStateFactory: PermissionStateFactory,
  scope: ComponentScope<KeyUiComponent>,
  rp: ResourceProvider,
  toaster: Toaster
): StateFlow<WriteSecureSettingsPcInstructionsModel> = scope.state(
  WriteSecureSettingsPcInstructionsModel(packageName = buildInfo.packageName)
) {
  state
    .flatMapLatest { currentState ->
      if (currentState.currentStep != currentState.completedStep) flowOf(false)
      else when (currentState.completedStep) {
        1 -> developerModeSetting.data.map { it != 0 }
        2 -> adbEnabledSetting.data.map { it != 0 }
        3 -> isCharging.map { it.value }
        4 -> flow {
          while (true) {
            emit(permissionStateFactory(listOf(key.permissionKey)).first())
            delay(1000)
          }
        }
        else -> flowOf(true)
      }
    }
    .update { copy(canContinueStep = it) }

  action(WriteSecureSettingsPcInstructionsModel.continueStep()) {
    if (state.first().completedStep == 4)
      navigator.pop(key, true)
    else
      update { copy(currentStep = completedStep + 1, completedStep = completedStep + 1) }
  }

  action(WriteSecureSettingsPcInstructionsModel.openStep()) {
    update { copy(currentStep = it) }
  }

  action(WriteSecureSettingsPcInstructionsModel.cancelStep()) {
    update { copy(currentStep = completedStep - 1, completedStep = completedStep - 1) }
  }

  action(WriteSecureSettingsPcInstructionsModel.openPhoneInfo()) {
    race(
      { developerModeSetting.data.first { it != 0 } },
      {
        navigator.push(Intent(Settings.ACTION_DEVICE_INFO_SETTINGS).toIntentKey())
          ?.onFailure { showToast(R.string.open_phone_info_failed) }
      }
    )
    appUiStarter()
  }

  action(WriteSecureSettingsPcInstructionsModel.openDeveloperSettings()) {
    race(
      { adbEnabledSetting.data.first { it != 0 } },
      { navigator.push(Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS).toIntentKey()) }
    )
    appUiStarter()
  }
}
