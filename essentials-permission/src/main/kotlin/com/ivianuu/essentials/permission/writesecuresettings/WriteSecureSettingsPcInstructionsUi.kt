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
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ivianuu.essentials.BuildInfo
import com.ivianuu.essentials.clipboard.Clipboard
import com.ivianuu.essentials.coroutines.timer
import com.ivianuu.essentials.optics.Optics
import com.ivianuu.essentials.permission.PermissionStateFactory
import com.ivianuu.essentials.permission.R
import com.ivianuu.essentials.store.action
import com.ivianuu.essentials.store.state
import com.ivianuu.essentials.ui.common.SimpleListScreen
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.KeyUiScope
import com.ivianuu.essentials.ui.navigation.ModelKeyUi
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.ui.navigation.toIntentKey
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.common.TypeKey
import com.ivianuu.injekt.coroutines.NamedCoroutineScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.take
import kotlin.time.milliseconds

data class WriteSecureSettingsPcInstructionsKey(
  val permissionKey: TypeKey<WriteSecureSettingsPermission>
) : Key<Unit>

@Provide val writeSecureSettingsPcInstructionsUi: ModelKeyUi<WriteSecureSettingsPcInstructionsKey,
    WriteSecureSettingsPcInstructionsModel> = {
  SimpleListScreen(R.string.es_secure_settings_pc_instructions_title) {
    item {
      SecureSettingsHeader(
        text = stringResource(R.string.es_secure_settings_pc_instructions_desc)
      )
    }

    item {
      ListItem(
        leading = { StepBadge(1) },
        title = {
          Text(
            textResId = R.string.es_secure_settings_step_1,
            style = MaterialTheme.typography.body2
          )
        },
        trailing = { Icon(R.drawable.es_ic_launch) },
        onClick = model.openPhoneInfo
      )
    }

    item {
      ListItem(
        leading = { StepBadge(2) },
        title = {
          Text(
            textResId = R.string.es_secure_settings_step_2,
            style = MaterialTheme.typography.body2
          )
        },
        trailing = { Icon(R.drawable.es_ic_launch) },
        onClick = model.openDeveloperSettings
      )
    }

    item {
      ListItem(
        leading = { StepBadge(3) },
        title = {
          Text(
            textResId = R.string.es_secure_settings_step_3,
            style = MaterialTheme.typography.body2
          )
        }
      )
    }

    item {
      ListItem(
        leading = { StepBadge(4) },
        title = {
          Text(
            textResId = R.string.es_secure_settings_step_4,
            style = MaterialTheme.typography.body2
          )
        },
        trailing = { Icon(R.drawable.es_ic_content_copy) },
        onClick = model.copyWebAdbLink
      )
    }

    item {
      ListItem(
        leading = { StepBadge(5) },
        title = {
          Text(
            textResId = R.string.es_secure_settings_step_5,
            style = MaterialTheme.typography.body2
          )
        }
      )
    }

    item {
      ListItem(
        leading = { StepBadge(6) },
        title = {
          Text(
            textResId = R.string.es_secure_settings_step_6,
            style = MaterialTheme.typography.body2
          )
        }
      )
    }

    item {
      ListItem(
        leading = { StepBadge(7) },
        title = {
          Text(
            textResId = R.string.es_secure_settings_step_7,
            style = MaterialTheme.typography.body2
          )
        },
      )
    }

    item {
      ListItem(
        leading = { StepBadge(8) },
        title = {
          Text(
            text = stringResource(
              R.string.es_secure_settings_step_8,
              model.packageName
            ),
            style = MaterialTheme.typography.body2
          )
        },
        trailing = { Icon(R.drawable.es_ic_content_copy) },
        onClick = model.copyAdbCommand
      )
    }
  }
}

@Composable fun StepBadge(step: Int) {
  Box(
    modifier = Modifier
      .size(24.dp)
      .background(MaterialTheme.colors.secondary, CircleShape),
    contentAlignment = Alignment.Center
  ) {
    Text(
      text = step.toString(),
      style = MaterialTheme.typography.caption.copy(color = MaterialTheme.colors.onSecondary),
      textAlign = TextAlign.Center
    )
  }
}

@Optics data class WriteSecureSettingsPcInstructionsModel(
  val packageName: String = "",
  val openPhoneInfo: () -> Unit = {},
  val openDeveloperSettings: () -> Unit = {},
  val copyWebAdbLink: () -> Unit = {},
  val copyAdbCommand: () -> Unit = {}
) {
  val secureSettingsAdbCommand = "pm grant $packageName android.permission.WRITE_SECURE_SETTINGS"
}

@Provide fun writeSecureSettingsPcInstructionsModel(
  buildInfo: BuildInfo,
  clipboard: Clipboard,
  key: WriteSecureSettingsPcInstructionsKey,
  navigator: Navigator,
  permissionStateFactory: PermissionStateFactory,
  scope: NamedCoroutineScope<KeyUiScope>
): StateFlow<WriteSecureSettingsPcInstructionsModel> = scope.state(
  WriteSecureSettingsPcInstructionsModel(buildInfo.packageName)
) {
  timer(200.milliseconds)
    .flatMapLatest { permissionStateFactory(listOf(key.permissionKey)) }
    .filter { it }
    .take(1)
    .onEach { navigator.pop(key) }
    .launchIn(this)

  action(WriteSecureSettingsPcInstructionsModel.openPhoneInfo()) {
    navigator.push(Intent(Settings.ACTION_DEVICE_INFO_SETTINGS).toIntentKey())
  }

  action(WriteSecureSettingsPcInstructionsModel.openDeveloperSettings()) {
    navigator.push(Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS).toIntentKey())
  }

  action(WriteSecureSettingsPcInstructionsModel.copyWebAdbLink()) {
    clipboard.updateText("https://webadb.com")
  }

  action(WriteSecureSettingsPcInstructionsModel.copyAdbCommand()) {
    clipboard.updateText(state.first().secureSettingsAdbCommand, true)
  }
}
