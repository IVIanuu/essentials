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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ivianuu.essentials.BuildInfo
import com.ivianuu.essentials.android.settings.AndroidSettingModule
import com.ivianuu.essentials.android.settings.AndroidSettingsType
import com.ivianuu.essentials.coroutines.race
import com.ivianuu.essentials.data.DataStore
import com.ivianuu.essentials.onFailure
import com.ivianuu.essentials.permission.PermissionStateFactory
import com.ivianuu.essentials.permission.R
import com.ivianuu.essentials.state.action
import com.ivianuu.essentials.state.produceValue
import com.ivianuu.essentials.state.valueFromFlow
import com.ivianuu.essentials.ui.common.SimpleListScreen
import com.ivianuu.essentials.ui.material.Button
import com.ivianuu.essentials.ui.material.OutlinedButton
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.KeyUiContext
import com.ivianuu.essentials.ui.navigation.ModelKeyUi
import com.ivianuu.essentials.ui.navigation.toIntentKey
import com.ivianuu.essentials.ui.stepper.Step
import com.ivianuu.essentials.util.AppUiStarter
import com.ivianuu.essentials.util.BroadcastsFactory
import com.ivianuu.essentials.util.ToastContext
import com.ivianuu.essentials.util.showToast
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.Tag
import com.ivianuu.injekt.common.TypeKey
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
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

data class WriteSecureSettingsPcInstructionsModel(
  val currentStep: Int,
  val completedStep: Int,
  val canContinueStep: Boolean,
  val packageName: String,
  val openPhoneInfo: () -> Unit,
  val openDeveloperSettings: () -> Unit,
  val continueStep: () -> Unit,
  val openStep: (Int) -> Unit,
  val cancelStep: () -> Unit
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

@Provide @Composable fun writeSecureSettingsPcInstructionsModel(
  adbEnabledSetting: DataStore<AdbEnabled>,
  appUiStarter: AppUiStarter,
  buildInfo: BuildInfo,
  developerModeSetting: DataStore<DeveloperMode>,
  isCharging: Flow<IsCharging>,
  permissionStateFactory: PermissionStateFactory,
  T: ToastContext,
  ctx: KeyUiContext<WriteSecureSettingsPcInstructionsKey>
): WriteSecureSettingsPcInstructionsModel {
  var currentStep by remember { mutableStateOf(1) }
  var completedStep by remember { mutableStateOf(1) }

  val canContinueStep = if (currentStep != completedStep) false
  else when (completedStep) {
    1 -> valueFromFlow(false) { developerModeSetting.data.map { it != 0 } }
    2 -> valueFromFlow(false) { adbEnabledSetting.data.map { it != 0 } }
    3 -> valueFromFlow(false) { isCharging.map { it.value } }
    4 -> produceValue(false) {
      while (true) {
        value = permissionStateFactory(listOf(ctx.key.permissionKey)).first()
        delay(1000)
      }
    }
    else -> true
  }

  return WriteSecureSettingsPcInstructionsModel(
    packageName = buildInfo.packageName,
    currentStep = currentStep,
    completedStep = completedStep,
    canContinueStep = canContinueStep,
    continueStep = action {
      if (completedStep == 4)
        ctx.navigator.pop(ctx.key, true)
      else {
        completedStep++
        currentStep = completedStep
      }
    },
    openStep = { currentStep = it },
    cancelStep = {
      currentStep = completedStep - 1
      completedStep = currentStep
    },
    openPhoneInfo = action {
      race(
        { developerModeSetting.data.first { it != 0 } },
        {
          ctx.navigator.push(Intent(Settings.ACTION_DEVICE_INFO_SETTINGS).toIntentKey())
            ?.onFailure { showToast(R.string.open_phone_info_failed) }
        }
      )
      appUiStarter()
    },
    openDeveloperSettings = action {
      race(
        { adbEnabledSetting.data.first { it != 0 } },
        { ctx.navigator.push(Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS).toIntentKey()) }
      )
      appUiStarter()
    }
  )
}
