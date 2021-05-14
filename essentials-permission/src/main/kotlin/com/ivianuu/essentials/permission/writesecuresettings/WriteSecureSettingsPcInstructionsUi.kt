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

package com.ivianuu.essentials.permission.writesecuresettings

import androidx.compose.foundation.lazy.*
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.ui.res.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.clipboard.*
import com.ivianuu.essentials.coroutines.*
import com.ivianuu.essentials.optics.*
import com.ivianuu.essentials.permission.*
import com.ivianuu.essentials.permission.R
import com.ivianuu.essentials.store.*
import com.ivianuu.essentials.ui.core.*
import com.ivianuu.essentials.ui.material.*
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.common.*
import com.ivianuu.injekt.coroutines.*
import com.ivianuu.injekt.scope.*
import kotlinx.coroutines.flow.*
import kotlin.time.*

data class WriteSecureSettingsPcInstructionsKey(
  val permissionKey: TypeKey<WriteSecureSettingsPermission>
) : Key<Nothing>

@Given val writeSecureSettingsPcInstructionsUi: ModelKeyUi<WriteSecureSettingsPcInstructionsKey,
    WriteSecureSettingsPcInstructionsModel> = {
  Scaffold(
    topBar = { TopAppBar(title = { Text(stringResource(R.string.es_title_secure_settings_pc_instructions)) }) }
  ) {
    LazyColumn(contentPadding = localVerticalInsetsPadding()) {
      item {
        SecureSettingsHeader(
          text = stringResource(R.string.es_pref_secure_settings_pc_instructions_header_summary)
        )
      }
      item {
        ListItem(
          title = { Text(stringResource(R.string.es_pref_secure_settings_step_1)) },
          subtitle = { Text(stringResource(R.string.es_pref_secure_settings_step_1_summary)) }
        )
      }
      item {
        ListItem(
          title = { Text(stringResource(R.string.es_pref_secure_settings_step_2)) },
          subtitle = { Text(stringResource(R.string.es_pref_secure_settings_step_2_summary)) }
        )
      }
      item {
        ListItem(
          title = { Text(stringResource(R.string.es_pref_secure_settings_step_3)) },
          subtitle = { Text(stringResource(R.string.es_pref_secure_settings_step_3_summary)) }
        )
      }
      item {
        ListItem(
          leading = { Icon(painterResource(R.drawable.es_ic_link), null) },
          title = { Text(stringResource(R.string.es_pref_secure_settings_link_gadget_hacks_summary)) },
          onClick = model.openGadgetHacksTutorial
        )
      }
      item {
        ListItem(
          leading = { Icon(painterResource(R.drawable.es_ic_link), null) },
          title = { Text(stringResource(R.string.es_pref_secure_settings_link_lifehacker_summary)) },
          onClick = model.openLifehackerTutorial
        )
      }
      item {
        ListItem(
          leading = { Icon(painterResource(R.drawable.es_ic_link), null) },
          title = { Text(stringResource(R.string.es_pref_secure_settings_link_xda_summary)) },
          onClick = model.openXdaTutorial
        )
      }
      item {
        ListItem(
          title = { Text(stringResource(R.string.es_pref_secure_settings_step_4)) },
          subtitle = {
            Text(
              stringResource(
                R.string.es_pref_secure_settings_step_4_summary,
                model.packageName
              )
            )
          },
          onClick = model.copyAdbCommand
        )
      }
    }
  }
}

@Optics data class WriteSecureSettingsPcInstructionsModel(
  val packageName: String = "",
  val copyAdbCommand: () -> Unit = {},
  val openGadgetHacksTutorial: () -> Unit = {},
  val openLifehackerTutorial: () -> Unit = {},
  val openXdaTutorial: () -> Unit = {}
) {
  val secureSettingsAdbCommand =
    "adb shell pm grant $packageName android.permission.WRITE_SECURE_SETTINGS"

  companion object {
    @Given
    fun initial(@Given buildInfo: BuildInfo): @Initial WriteSecureSettingsPcInstructionsModel =
      WriteSecureSettingsPcInstructionsModel(packageName = buildInfo.packageName)
  }
}

@Given fun writeSecureSettingsPcInstructionsModel(
  @Given initial: @Initial WriteSecureSettingsPcInstructionsModel,
  @Given key: WriteSecureSettingsPcInstructionsKey,
  @Given navigator: Navigator,
  @Given permissionStateFactory: PermissionStateFactory,
  @Given scope: GivenCoroutineScope<KeyUiGivenScope>,
  @Given updateClipboardText: UpdateClipboardTextUseCase
): @Scoped<KeyUiGivenScope> StateFlow<WriteSecureSettingsPcInstructionsModel> = scope.state(
  initial
) {
  timer(200.milliseconds)
    .flatMapLatest { permissionStateFactory(listOf(key.permissionKey)) }
    .filter { it }
    .take(1)
    .onEach { navigator.pop(key) }
    .launchIn(this)
  action(WriteSecureSettingsPcInstructionsModel.copyAdbCommand()) {
    updateClipboardText(state.first().secureSettingsAdbCommand)
  }
  action(WriteSecureSettingsPcInstructionsModel.openGadgetHacksTutorial()) {
    navigator.push(UrlKey("https://youtu.be/CDuxcrrWLnY"))
  }
  action(WriteSecureSettingsPcInstructionsModel.openLifehackerTutorial()) {
    navigator.push(
      UrlKey("https://lifehacker.com/the-easiest-way-to-install-androids-adb-and-fastboot-to-1586992378")
    )
  }
  action(WriteSecureSettingsPcInstructionsModel.openXdaTutorial()) {
    navigator.push(
      UrlKey("https://www.xda-developers.com/install-adb-windows-macos-linux/")
    )
  }
}
