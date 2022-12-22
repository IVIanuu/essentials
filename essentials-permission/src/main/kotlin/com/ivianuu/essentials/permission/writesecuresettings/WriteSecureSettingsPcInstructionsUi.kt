/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.permission.writesecuresettings

import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
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
import com.ivianuu.essentials.state.bind
import com.ivianuu.essentials.state.produce
import com.ivianuu.essentials.ui.common.SimpleListScreen
import com.ivianuu.essentials.ui.material.Button
import com.ivianuu.essentials.ui.material.OutlinedButton
import com.ivianuu.essentials.ui.navigation.CriticalUserFlowKey
import com.ivianuu.essentials.ui.navigation.DefaultIntentKey
import com.ivianuu.essentials.ui.navigation.KeyUiContext
import com.ivianuu.essentials.ui.navigation.Model
import com.ivianuu.essentials.ui.navigation.ModelKeyUi
import com.ivianuu.essentials.ui.navigation.pop
import com.ivianuu.essentials.ui.navigation.push
import com.ivianuu.essentials.ui.stepper.Step
import com.ivianuu.essentials.util.AppUiStarter
import com.ivianuu.essentials.util.ToastContext
import com.ivianuu.essentials.util.showToast
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.Tag
import com.ivianuu.injekt.common.TypeKey
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

data class WriteSecureSettingsPcInstructionsKey(
  val permissionKey: TypeKey<WriteSecureSettingsPermission>
) : CriticalUserFlowKey<Boolean>

@Provide val writeSecureSettingsPcInstructionsUi =
  ModelKeyUi<WriteSecureSettingsPcInstructionsKey, WriteSecureSettingsPcInstructionsModel> {
  SimpleListScreen(R.string.es_secure_settings_pc_instructions_title) {
    item {
      SecureSettingsHeader(
        text = stringResource(R.string.es_secure_settings_pc_instructions_desc)
      )
    }

    item {
      Step(
        step = 1,
        isCompleted = completedStep > 1,
        isCurrent = currentStep == 1,
        onClick = { openStep(1) },
        title = { Text(R.string.es_secure_settings_step_1_title) },
        content = {
          Text(
            textResId = R.string.es_secure_settings_step_1_content,
            style = MaterialTheme.typography.body2
          )
        },
        actions = {
          Button(
            onClick = continueStep,
            enabled = canContinueStep
          ) { Text(R.string.es_continue) }
          OutlinedButton(onClick = openPhoneInfo) { Text(R.string.open_phone_info) }
        }
      )
    }

    item {
      Step(
        step = 2,
        isCompleted = completedStep > 2,
        isCurrent = currentStep == 2,
        onClick = { openStep(2) },
        title = { Text(R.string.es_secure_settings_step_2_title) },
        content = {
          Text(
            textResId = R.string.es_secure_settings_step_2_content,
            style = MaterialTheme.typography.body2
          )
        },
        actions = {
          Button(onClick = continueStep, enabled = canContinueStep) { Text(R.string.es_continue) }
          OutlinedButton(onClick = openDeveloperSettings) { Text(R.string.open_developer_settings) }
        }
      )
    }

    item {
      Step(
        step = 3,
        isCompleted = completedStep > 3,
        isCurrent = currentStep == 3,
        onClick = { openStep(3) },
        title = { Text(R.string.es_secure_settings_step_3_title) },
        content = { Text(R.string.es_secure_settings_step_3_content) },
        actions = {
          Button(onClick = continueStep, enabled = canContinueStep) { Text(R.string.es_continue) }
        }
      )
    }

    item {
      Step(
        step = 4,
        isCompleted = completedStep > 4,
        isCurrent = currentStep == 4,
        onClick = { openStep(4) },
        title = { Text(R.string.es_secure_settings_step_4_title) },
        content = {
          Text(R.string.es_secure_settings_step_4_content)

          Text(
            modifier = Modifier
              .padding(top = 8.dp)
              .background(LocalContentColor.current.copy(alpha = 0.12f), RoundedCornerShape(4.dp))
              .padding(4.dp),
            text = adbCommand,
            style = MaterialTheme.typography.body2.copy(fontSize = 14.sp)
          )
        },
        actions = {
          Button(
            onClick = continueStep,
            enabled = canContinueStep
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

context(KeyUiContext<WriteSecureSettingsPcInstructionsKey>, ToastContext) @Provide fun writeSecureSettingsPcInstructionsModel(
  adbEnabledSetting: DataStore<AdbEnabled>,
  appUiStarter: AppUiStarter,
  buildInfo: BuildInfo,
  developerModeSetting: DataStore<DeveloperMode>,
  permissionStateFactory: PermissionStateFactory
) = Model {
  var currentStep by remember { mutableStateOf(1) }
  var completedStep by remember { mutableStateOf(1) }

  val canContinueStep = if (currentStep != completedStep) false
  else when (completedStep) {
    1 -> developerModeSetting.data.map { it != 0 }.bind(false)
    2 -> adbEnabledSetting.data.map { it != 0 }.bind(false)
    3 -> true
    4 -> produce(false) {
      while (true) {
        value = permissionStateFactory(listOf(key.permissionKey)).first()
        delay(1000)
      }
    }
    else -> true
  }

  WriteSecureSettingsPcInstructionsModel(
    packageName = buildInfo.packageName,
    currentStep = currentStep,
    completedStep = completedStep,
    canContinueStep = canContinueStep,
    continueStep = action {
      if (completedStep == 4)
        navigator.pop(key, true)
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
          navigator.push(DefaultIntentKey(Intent(Settings.ACTION_DEVICE_INFO_SETTINGS)))
            ?.onFailure { showToast(R.string.open_phone_info_failed) }
        }
      )
      appUiStarter()
    },
    openDeveloperSettings = action {
      race(
        { adbEnabledSetting.data.first { it != 0 } },
        { navigator.push(DefaultIntentKey(Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS))) }
      )
      appUiStarter()
    }
  )
}
