/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.permission.writesecuresettings

import android.content.*
import android.provider.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.res.*
import androidx.compose.ui.unit.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.android.R
import com.ivianuu.essentials.compose.*
import com.ivianuu.essentials.data.*
import com.ivianuu.essentials.permission.*
import com.ivianuu.essentials.shell.*
import com.ivianuu.essentials.ui.common.*
import com.ivianuu.essentials.ui.material.*
import com.ivianuu.essentials.ui.material.Button
import com.ivianuu.essentials.ui.material.OutlinedButton
import com.ivianuu.essentials.ui.material.TextButton
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.essentials.ui.stepper.*
import com.ivianuu.essentials.util.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.common.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import splitties.coroutines.*

class WriteSecureSettingsScreen(
  val permissionKey: TypeKey<WriteSecureSettingsPermission>
) : CriticalUserFlowScreen<Boolean>

@Provide val writeSecureSettingsUi =
  Ui<WriteSecureSettingsScreen, WriteSecureSettingsState> { state ->
    ScreenScaffold(
      topBar = { AppBar { Text(stringResource(R.string.secure_settings_title)) } },
      bottomBar = {
        Snackbar(
          modifier = Modifier.padding(16.dp),
          action = {
            TextButton(onClick = state.grantPermissionsViaRoot) {
              Text(stringResource(R.string.secure_settings_use_root_action))
            }
          }
        ) {
          Text(stringResource(R.string.secure_settings_use_root))
        }
      }
    ) {
      VerticalList {
        item {
          Text(
            stringResource(R.string.secure_settings_desc),
            modifier = Modifier.padding(all = 16.dp),
            style = MaterialTheme.typography.body2
          )
        }

        item {
          Step(
            step = 1,
            isCompleted = state.completedStep > 1,
            isCurrent = state.currentStep == 1,
            onClick = { state.openStep(1) },
            title = { Text(stringResource(R.string.secure_settings_step_1_title)) },
            content = {
              Text(
                text = stringResource(R.string.secure_settings_step_1_content),
                style = MaterialTheme.typography.body2
              )
            },
            actions = {
              Button(
                onClick = state.continueStep,
                enabled = state.canContinueStep
              ) { Text(stringResource(R.string._continue)) }
              OutlinedButton(onClick = state.openPhoneInfo) { Text(stringResource(R.string.open_phone_info)) }
            }
          )
        }

        item {
          Step(
            step = 2,
            isCompleted = state.completedStep > 2,
            isCurrent = state.currentStep == 2,
            onClick = { state.openStep(2) },
            title = { Text(stringResource(R.string.secure_settings_step_2_title)) },
            content = {
              Text(
                text = stringResource(R.string.secure_settings_step_2_content),
                style = MaterialTheme.typography.body2
              )
            },
            actions = {
              Button(
                onClick = state.continueStep,
                enabled = state.canContinueStep
              ) { Text(stringResource(R.string._continue)) }
              OutlinedButton(onClick = state.openDeveloperSettings) {
                Text(stringResource(R.string.open_developer_settings))
              }
            }
          )
        }

        item {
          Step(
            step = 3,
            isCompleted = state.completedStep > 3,
            isCurrent = state.currentStep == 3,
            onClick = { state.openStep(3) },
            title = { Text(stringResource(R.string.secure_settings_step_3_title)) },
            content = { Text(stringResource(R.string.secure_settings_step_3_content)) },
            actions = {
              Button(
                onClick = state.continueStep,
                enabled = state.canContinueStep
              ) { Text(stringResource(R.string._continue)) }
            }
          )
        }

        item {
          Step(
            step = 4,
            isCompleted = state.completedStep > 4,
            isCurrent = state.currentStep == 4,
            onClick = { state.openStep(4) },
            title = { Text(stringResource(R.string.secure_settings_step_4_title)) },
            content = {
              Text(stringResource(R.string.secure_settings_step_4_content))

              Text(
                modifier = Modifier
                  .padding(top = 8.dp)
                  .background(
                    LocalContentColor.current.copy(alpha = 0.12f),
                    RoundedCornerShape(4.dp)
                  )
                  .padding(4.dp),
                text = state.adbCommand,
                style = MaterialTheme.typography.body2.copy(fontSize = 14.sp)
              )
            },
            actions = {
              Button(
                onClick = state.continueStep,
                enabled = state.canContinueStep
              ) { Text(stringResource(R.string.complete)) }
            }
          )
        }
      }
    }
  }

data class WriteSecureSettingsState(
  val currentStep: Int,
  val completedStep: Int,
  val canContinueStep: Boolean,
  val packageName: String,
  val openPhoneInfo: () -> Unit,
  val openDeveloperSettings: () -> Unit,
  val continueStep: () -> Unit,
  val openStep: (Int) -> Unit,
  val cancelStep: () -> Unit,
  val grantPermissionsViaRoot: () -> Unit
) {
  val adbCommand = "pm grant $packageName android.permission.WRITE_SECURE_SETTINGS"
}

@Tag annotation class DeveloperModeTag
typealias DeveloperMode = @DeveloperModeTag Int

@Provide val developerModeSettingModule = AndroidSettingModule<DeveloperMode, Int>(
  Settings.Global.DEVELOPMENT_SETTINGS_ENABLED,
  AndroidSettingsType.GLOBAL,
  0
)

@Tag annotation class AdbEnabledTag
typealias AdbEnabled = @AdbEnabledTag Int

@Provide val adbEnabledSettingModule = AndroidSettingModule<AdbEnabled, Int>(
  Settings.Global.ADB_ENABLED,
  AndroidSettingsType.GLOBAL,
  0
)

@Provide fun writeSecureSettingsPcInstructionsPresenter(
  adbEnabledDataStore: DataStore<AdbEnabled>,
  appUiStarter: AppUiStarter,
  appConfig: AppConfig,
  navigator: Navigator,
  developerModeDataStore: DataStore<DeveloperMode>,
  permissionManager: PermissionManager,
  screen: WriteSecureSettingsScreen,
  shell: Shell,
  toaster: Toaster
) = Presenter {
  var currentStep by remember { mutableStateOf(1) }
  var completedStep by remember { mutableStateOf(1) }

  val canContinueStep = if (currentStep != completedStep) false
  else produceState(false, completedStep) {
    when (completedStep) {
      1 -> developerModeDataStore.data.map { it != 0 }.collect { value = it }
      2 -> adbEnabledDataStore.data.map { it != 0 }.collect { value = it }
      3 -> value = true
      4 -> while (true) {
        value = permissionManager.permissionState(listOf(screen.permissionKey)).first()
        delay(1000)
      }
      else -> value = true
    }
  }.value

  WriteSecureSettingsState(
    packageName = appConfig.packageName,
    currentStep = currentStep,
    completedStep = completedStep,
    canContinueStep = canContinueStep,
    continueStep = action {
      if (completedStep == 4)
        navigator.pop(screen, true)
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
      raceOf(
        {
          navigator.push(DefaultIntentScreen(Intent(Settings.ACTION_DEVICE_INFO_SETTINGS)))
            ?.onLeft { toaster(R.string.open_phone_info_failed) }
        },
        { developerModeDataStore.data.first { it != 0 } }
      )
      appUiStarter()
    },
    openDeveloperSettings = action {
      raceOf(
        {
          navigator.push(DefaultIntentScreen(Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS)))
            ?.onLeft { toaster(R.string.open_developer_settings_failed) }
        },
        { adbEnabledDataStore.data.first { it != 0 } }
      )
      appUiStarter()
    },
    grantPermissionsViaRoot = action {
      shell.run("pm grant ${appConfig.packageName} android.permission.WRITE_SECURE_SETTINGS")
        .onRight {
          if (permissionManager.permissionState(listOf(screen.permissionKey)).first()) {
            toaster(R.string.secure_settings_permission_granted)
            navigator.pop(screen)
          }
        }
        .onLeft {
          it.printStackTrace()
          toaster(R.string.secure_settings_no_root)
        }
    }
  )
}
