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
import androidx.compose.material.Snackbar
import androidx.compose.material.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import arrow.fx.coroutines.raceN
import com.ivianuu.essentials.AppConfig
import com.ivianuu.essentials.android.R
import com.ivianuu.essentials.compose.action
import com.ivianuu.essentials.data.AndroidSettingModule
import com.ivianuu.essentials.data.AndroidSettingsType
import com.ivianuu.essentials.data.DataStore
import com.ivianuu.essentials.permission.PermissionManager
import com.ivianuu.essentials.shell.Shell
import com.ivianuu.essentials.ui.common.VerticalList
import com.ivianuu.essentials.ui.material.AppBar
import com.ivianuu.essentials.ui.material.Button
import com.ivianuu.essentials.ui.material.OutlinedButton
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TextButton
import com.ivianuu.essentials.ui.navigation.AppUiStarter
import com.ivianuu.essentials.ui.navigation.CriticalUserFlowScreen
import com.ivianuu.essentials.ui.navigation.DefaultIntentScreen
import com.ivianuu.essentials.ui.navigation.Presenter
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.ui.navigation.Ui
import com.ivianuu.essentials.ui.navigation.pop
import com.ivianuu.essentials.ui.navigation.push
import com.ivianuu.essentials.ui.stepper.Step
import com.ivianuu.essentials.util.Toaster
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.Tag
import com.ivianuu.injekt.common.TypeKey
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class WriteSecureSettingsScreen(
  val permissionKey: TypeKey<WriteSecureSettingsPermission>
) : CriticalUserFlowScreen<Boolean>

@Provide val writeSecureSettingsUi =
  Ui<WriteSecureSettingsScreen, WriteSecureSettingsState> { state ->
    Scaffold(
      topBar = { AppBar { Text(stringResource(R.string.es_secure_settings_title)) } },
      bottomBar = {
        Snackbar(
          modifier = Modifier.padding(16.dp),
          action = {
            TextButton(onClick = state.grantPermissionsViaRoot) {
              Text(stringResource(R.string.es_secure_settings_use_root_action))
            }
          }
        ) {
          Text(stringResource(R.string.es_secure_settings_use_root))
        }
      }
    ) {
      VerticalList {
        item {
          Text(
            stringResource(R.string.es_secure_settings_desc),
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
            title = { Text(stringResource(R.string.es_secure_settings_step_1_title)) },
            content = {
              Text(
                text = stringResource(R.string.es_secure_settings_step_1_content),
                style = MaterialTheme.typography.body2
              )
            },
            actions = {
              Button(
                onClick = state.continueStep,
                enabled = state.canContinueStep
              ) { Text(stringResource(R.string.es_continue)) }
              OutlinedButton(onClick = state.openPhoneInfo) { Text(stringResource(R.string.es_open_phone_info)) }
            }
          )
        }

        item {
          Step(
            step = 2,
            isCompleted = state.completedStep > 2,
            isCurrent = state.currentStep == 2,
            onClick = { state.openStep(2) },
            title = { Text(stringResource(R.string.es_secure_settings_step_2_title)) },
            content = {
              Text(
                text = stringResource(R.string.es_secure_settings_step_2_content),
                style = MaterialTheme.typography.body2
              )
            },
            actions = {
              Button(
                onClick = state.continueStep,
                enabled = state.canContinueStep
              ) { Text(stringResource(R.string.es_continue)) }
              OutlinedButton(onClick = state.openDeveloperSettings) {
                Text(stringResource(R.string.es_open_developer_settings))
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
            title = { Text(stringResource(R.string.es_secure_settings_step_3_title)) },
            content = { Text(stringResource(R.string.es_secure_settings_step_3_content)) },
            actions = {
              Button(
                onClick = state.continueStep,
                enabled = state.canContinueStep
              ) { Text(stringResource(R.string.es_continue)) }
            }
          )
        }

        item {
          Step(
            step = 4,
            isCompleted = state.completedStep > 4,
            isCurrent = state.currentStep == 4,
            onClick = { state.openStep(4) },
            title = { Text(stringResource(R.string.es_secure_settings_step_4_title)) },
            content = {
              Text(stringResource(R.string.es_secure_settings_step_4_content))

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
              ) { Text(stringResource(R.string.es_complete)) }
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
      raceN(
        {
          navigator.push(DefaultIntentScreen(Intent(Settings.ACTION_DEVICE_INFO_SETTINGS)))
            ?.onLeft { toaster(R.string.es_open_phone_info_failed) }
        },
        { developerModeDataStore.data.first { it != 0 } }
      )
      appUiStarter()
    },
    openDeveloperSettings = action {
      raceN(
        {
          navigator.push(DefaultIntentScreen(Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS)))
            ?.onLeft { toaster(R.string.es_open_developer_settings_failed) }
        },
        { adbEnabledDataStore.data.first { it != 0 } }
      )
      appUiStarter()
    },
    grantPermissionsViaRoot = action {
      shell.run("pm grant ${appConfig.packageName} android.permission.WRITE_SECURE_SETTINGS")
        .onRight {
          if (permissionManager.permissionState(listOf(screen.permissionKey)).first()) {
            toaster(R.string.es_secure_settings_permission_granted)
            navigator.pop(screen)
          }
        }
        .onLeft {
          it.printStackTrace()
          toaster(R.string.es_secure_settings_no_root)
        }
    }
  )
}
