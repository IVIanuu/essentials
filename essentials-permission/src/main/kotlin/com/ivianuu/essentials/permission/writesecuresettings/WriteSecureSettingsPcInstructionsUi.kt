/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.permission.writesecuresettings

import android.content.*
import android.provider.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.ui.*
import androidx.compose.ui.res.*
import androidx.compose.ui.unit.*
import com.github.michaelbull.result.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.android.settings.*
import com.ivianuu.essentials.coroutines.*
import com.ivianuu.essentials.data.*
import com.ivianuu.essentials.permission.*
import com.ivianuu.essentials.permission.R
import com.ivianuu.essentials.state.*
import com.ivianuu.essentials.ui.common.*
import com.ivianuu.essentials.ui.material.Button
import com.ivianuu.essentials.ui.material.OutlinedButton
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.essentials.ui.stepper.*
import com.ivianuu.essentials.util.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.common.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

data class WriteSecureSettingsPcInstructionsKey(
  val permissionKey: TypeKey<WriteSecureSettingsPermission>
) : Key<Boolean>

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

@Provide fun writeSecureSettingsPcInstructionsModel(
  adbEnabledSetting: DataStore<AdbEnabled>,
  appUiStarter: AppUiStarter,
  buildInfo: BuildInfo,
  developerModeSetting: DataStore<DeveloperMode>,
  permissionStateFactory: PermissionStateFactory,
  T: ToastContext,
  ctx: KeyUiContext<WriteSecureSettingsPcInstructionsKey>
): WriteSecureSettingsPcInstructionsModel {
  var currentStep by memo { stateVar(1) }
  var completedStep by memo { stateVar(1) }

  val canContinueStep = if (currentStep != completedStep) false
  else when (completedStep) {
    1 -> developerModeSetting.data.map { it != 0 }.bind(false)
    2 -> adbEnabledSetting.data.map { it != 0 }.bind(false)
    3 -> true
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
