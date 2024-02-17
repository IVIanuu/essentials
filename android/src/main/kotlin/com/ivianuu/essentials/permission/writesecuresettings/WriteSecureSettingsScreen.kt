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
) : CriticalUserFlowScreen<Boolean> {
  @Provide companion object {
    @Provide fun ui(
      adbEnabledDataStore: DataStore<AdbEnabled>,
      appUiStarter: AppUiStarter,
      appConfig: AppConfig,
      navigator: Navigator,
      developerModeDataStore: DataStore<DeveloperMode>,
      permissionManager: PermissionManager,
      screen: WriteSecureSettingsScreen,
      shell: Shell,
      toaster: Toaster
    ) = Ui<WriteSecureSettingsScreen, Unit> {
      var currentStep by remember { mutableIntStateOf(1) }
      var completedStep by remember { mutableIntStateOf(1) }

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
      val continueStep = action {
        if (completedStep == 4)
          navigator.pop(screen, true)
        else {
          completedStep++
          currentStep = completedStep
        }
      }
      val openStep = { step: Int -> currentStep = step }

      ScreenScaffold(
        topBar = { AppBar { Text(stringResource(R.string.secure_settings_title)) } },
        bottomBar = {
          Snackbar(
            modifier = Modifier.padding(16.dp),
            action = {
              TextButton(onClick = scopedAction {
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
              }) {
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
              isCompleted = completedStep > 1,
              isCurrent = currentStep == 1,
              onClick = { openStep(1) },
              title = { Text(stringResource(R.string.secure_settings_step_1_title)) },
              content = {
                Text(
                  text = stringResource(R.string.secure_settings_step_1_content),
                  style = MaterialTheme.typography.body2
                )
              },
              actions = {
                Button(onClick = continueStep, enabled = canContinueStep) {
                  Text(stringResource(R.string._continue))
                }
                OutlinedButton(onClick = scopedAction {
                  raceOf(
                    {
                      navigator.push(Intent(Settings.ACTION_DEVICE_INFO_SETTINGS).asScreen())
                        ?.onLeft { toaster(R.string.open_phone_info_failed) }
                    },
                    { developerModeDataStore.data.first { it != 0 } }
                  )
                  appUiStarter()
                }) { Text(stringResource(R.string.open_phone_info)) }
              }
            )
          }

          item {
            Step(
              step = 2,
              isCompleted = completedStep > 2,
              isCurrent = currentStep == 2,
              onClick = { openStep(2) },
              title = { Text(stringResource(R.string.secure_settings_step_2_title)) },
              content = {
                Text(
                  text = stringResource(R.string.secure_settings_step_2_content),
                  style = MaterialTheme.typography.body2
                )
              },
              actions = {
                Button(onClick = continueStep, enabled = canContinueStep) {
                  Text(stringResource(R.string._continue))
                }
                OutlinedButton(onClick = scopedAction {
                  raceOf(
                    {
                      navigator.push(Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS).asScreen())
                        ?.onLeft { toaster(R.string.open_developer_settings_failed) }
                    },
                    { adbEnabledDataStore.data.first { it != 0 } }
                  )
                  appUiStarter()
                }) {
                  Text(stringResource(R.string.open_developer_settings))
                }
              }
            )
          }

          item {
            Step(
              step = 3,
              isCompleted = completedStep > 3,
              isCurrent = currentStep == 3,
              onClick = { openStep(3) },
              title = { Text(stringResource(R.string.secure_settings_step_3_title)) },
              content = { Text(stringResource(R.string.secure_settings_step_3_content)) },
              actions = {
                Button(onClick = continueStep, enabled = canContinueStep) {
                  Text(stringResource(R.string._continue))
                }
              }
            )
          }

          item {
            Step(
              step = 4,
              isCompleted = completedStep > 4,
              isCurrent = currentStep == 4,
              onClick = { openStep(4) },
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
                  text = "pm grant ${appConfig.packageName} android.permission.WRITE_SECURE_SETTINGS",
                  style = MaterialTheme.typography.body2.copy(fontSize = 14.sp)
                )
              },
              actions = {
                Button(onClick = continueStep, enabled = canContinueStep) {
                  Text(stringResource(R.string.complete))
                }
              }
            )
          }
        }
      }
    }
  }
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
