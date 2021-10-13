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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ProvideTextStyle
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ivianuu.essentials.BuildInfo
import com.ivianuu.essentials.android.settings.AndroidSettingModule
import com.ivianuu.essentials.android.settings.AndroidSettingsType
import com.ivianuu.essentials.coroutines.race
import com.ivianuu.essentials.data.DataStore
import com.ivianuu.essentials.optics.Optics
import com.ivianuu.essentials.permission.PermissionStateFactory
import com.ivianuu.essentials.permission.R
import com.ivianuu.essentials.store.action
import com.ivianuu.essentials.store.state
import com.ivianuu.essentials.ui.common.SimpleListScreen
import com.ivianuu.essentials.ui.material.Button
import com.ivianuu.essentials.ui.material.OutlinedButton
import com.ivianuu.essentials.ui.material.VerticalDivider
import com.ivianuu.essentials.ui.material.guessingContentColorFor
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.KeyUiScope
import com.ivianuu.essentials.ui.navigation.ModelKeyUi
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.ui.navigation.toIntentKey
import com.ivianuu.essentials.util.AppUiStarter
import com.ivianuu.essentials.util.BroadcastsFactory
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.common.TypeKey
import com.ivianuu.injekt.coroutines.NamedCoroutineScope
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
        isFinished = model.completedStep > 1,
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
          ) { Text("Continue") }
          OutlinedButton(onClick = model.openPhoneInfo) { Text("Open phone info") }
        }
      )
    }

    item {
      Step(
        step = 2,
        isFinished = model.completedStep > 2,
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
          ) { Text("Continue") }
          OutlinedButton(onClick = model.openDeveloperSettings) { Text("Open developer settings") }
        }
      )
    }

    item {
      Step(
        step = 3,
        isFinished = model.completedStep > 3,
        isCurrent = model.currentStep == 3,
        onClick = { model.openStep(3) },
        title = { Text(R.string.es_secure_settings_step_3_title) },
        content = { Text(R.string.es_secure_settings_step_3_content) },
        actions = {
          Button(
            onClick = model.continueStep,
            enabled = model.canContinueStep
          ) { Text("Continue") }
        }
      )
    }

    item {
      Step(
        step = 4,
        isFinished = model.completedStep > 4,
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
          ) { Text("Complete") }
        }
      )
    }
  }
}

@Composable fun Step(
  step: Int,
  isCurrent: Boolean,
  isFinished: Boolean,
  onClick: () -> Unit,
  title: @Composable () -> Unit,
  content: (@Composable () -> Unit)? = null,
  actions: @Composable () -> Unit
) {
  Column(
    modifier = Modifier.fillMaxWidth()
      .clickable(onClick = onClick)
      .padding(horizontal = 24.dp, vertical = 8.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth()
        .height(48.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      val backgroundColor = if (isCurrent || isFinished) MaterialTheme.colors.secondary
      else MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.disabled)
      Box(
        modifier = Modifier
          .size(24.dp)
          .background(backgroundColor, CircleShape),
        contentAlignment = Alignment.Center
      ) {
        CompositionLocalProvider(
          LocalContentColor provides guessingContentColorFor(backgroundColor)
        ) {
          if (isFinished) {
            Icon(
              modifier = Modifier.padding(4.dp),
              painterResId = R.drawable.es_ic_done
            )
          } else {
            Text(
              text = step.toString(),
              style = MaterialTheme.typography.caption
            )
          }
        }
      }

      Spacer(Modifier.width(16.dp))

      ProvideTextStyle(value = MaterialTheme.typography.subtitle1) {
        CompositionLocalProvider(
          LocalContentAlpha provides ContentAlpha.high,
          content = title
        )
      }
    }

    Spacer(Modifier.height(8.dp))

    Row {
      VerticalDivider(
        modifier = Modifier
          .padding(start = 24.dp)
      )

      Spacer(Modifier.width(40.dp))

      if (isCurrent) {
        Column {
          if (content != null) {
            ProvideTextStyle(value = MaterialTheme.typography.body2) {
              CompositionLocalProvider(
                LocalContentAlpha provides ContentAlpha.medium,
                content = content
              )
            }

            Spacer(Modifier.padding(bottom = 16.dp))
          }

          Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            actions()
          }
        }
      }
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

typealias DeveloperMode = Int

@Provide val developerModeSetting = AndroidSettingModule<DeveloperMode, Int>(
  Settings.Global.DEVELOPMENT_SETTINGS_ENABLED,
  AndroidSettingsType.GLOBAL,
  0
)

typealias AdbEnabled = Int

@Provide val adbEnabledSetting = AndroidSettingModule<AdbEnabled, Int>(
  Settings.Global.ADB_ENABLED,
  AndroidSettingsType.GLOBAL,
  0
)

typealias IsCharging = Boolean

@Provide fun isCharging(
  broadcastsFactory: BroadcastsFactory
): Flow<IsCharging> = broadcastsFactory(Intent.ACTION_BATTERY_CHANGED)
  .map {
    it.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ==
        BatteryManager.BATTERY_STATUS_CHARGING
  }
  .distinctUntilChanged()

@Provide fun writeSecureSettingsPcInstructionsModel(
  appUiStarter: AppUiStarter,
  buildInfo: BuildInfo,
  developerModeSetting: DataStore<DeveloperMode>,
  adbEnabledSetting: DataStore<AdbEnabled>,
  key: WriteSecureSettingsPcInstructionsKey,
  navigator: Navigator,
  permissionStateFactory: PermissionStateFactory,
  scope: NamedCoroutineScope<KeyUiScope>,
  isCharging: Flow<IsCharging>
): StateFlow<WriteSecureSettingsPcInstructionsModel> = scope.state(
  WriteSecureSettingsPcInstructionsModel(packageName = buildInfo.packageName)
) {
  state
    .flatMapLatest { currentState ->
      if (currentState.currentStep != currentState.completedStep) flowOf(false)
      else when (currentState.completedStep) {
        1 -> developerModeSetting.data.map { it != 0 }
        2 -> adbEnabledSetting.data.map { it != 0 }
        3 -> isCharging
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
      { navigator.push(Intent(Settings.ACTION_DEVICE_INFO_SETTINGS).toIntentKey()) }
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
