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

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.ui.res.stringResource
import com.ivianuu.essentials.BuildInfo
import com.ivianuu.essentials.clipboard.UpdateClipboardTextUseCase
import com.ivianuu.essentials.coroutines.timer
import com.ivianuu.essentials.optics.Optics
import com.ivianuu.essentials.permission.PermissionStateFactory
import com.ivianuu.essentials.permission.R
import com.ivianuu.essentials.store.Initial
import com.ivianuu.essentials.store.action
import com.ivianuu.essentials.store.state
import com.ivianuu.essentials.ui.core.localVerticalInsetsPadding
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.KeyUiScope
import com.ivianuu.essentials.ui.navigation.ModelKeyUi
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.ui.navigation.UrlKey
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.common.TypeKey
import com.ivianuu.injekt.coroutines.InjektCoroutineScope
import com.ivianuu.injekt.scope.Scoped
import kotlin.time.milliseconds
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.take

data class WriteSecureSettingsPcInstructionsKey(
  val permissionKey: TypeKey<WriteSecureSettingsPermission>
) : Key<Unit>

@Provide val writeSecureSettingsPcInstructionsUi: ModelKeyUi<WriteSecureSettingsPcInstructionsKey,
    WriteSecureSettingsPcInstructionsModel> = {
  Scaffold(
    topBar = { TopAppBar(title = { Text(R.string.es_title_secure_settings_pc_instructions) }) }
  ) {
    LazyColumn(contentPadding = localVerticalInsetsPadding()) {
      item {
        SecureSettingsHeader(
          text = stringResource(R.string.es_pref_secure_settings_pc_instructions_header_summary)
        )
      }
      item {
        ListItem(
          title = { Text(R.string.es_pref_secure_settings_step_1) },
          subtitle = { Text(R.string.es_pref_secure_settings_step_1_summary) }
        )
      }
      item {
        ListItem(
          title = { Text(R.string.es_pref_secure_settings_step_2) },
          subtitle = { Text(R.string.es_pref_secure_settings_step_2_summary) }
        )
      }
      item {
        ListItem(
          title = { Text(R.string.es_pref_secure_settings_step_3) },
          subtitle = { Text(R.string.es_pref_secure_settings_step_3_summary) }
        )
      }
      item {
        ListItem(
          leading = { Icon(R.drawable.es_ic_link) },
          title = { Text(R.string.es_pref_secure_settings_link_gadget_hacks_summary) },
          onClick = model.openGadgetHacksTutorial
        )
      }
      item {
        ListItem(
          leading = { Icon(R.drawable.es_ic_link) },
          title = { Text(R.string.es_pref_secure_settings_link_lifehacker_summary) },
          onClick = model.openLifehackerTutorial
        )
      }
      item {
        ListItem(
          leading = { Icon(R.drawable.es_ic_link) },
          title = { Text(R.string.es_pref_secure_settings_link_xda_summary) },
          onClick = model.openXdaTutorial
        )
      }
      item {
        ListItem(
          title = { Text(R.string.es_pref_secure_settings_step_4) },
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
    @Provide fun initial(buildInfo: BuildInfo): @Initial WriteSecureSettingsPcInstructionsModel =
      WriteSecureSettingsPcInstructionsModel(packageName = buildInfo.packageName)
  }
}

@Provide fun writeSecureSettingsPcInstructionsModel(
  initial: @Initial WriteSecureSettingsPcInstructionsModel,
  key: WriteSecureSettingsPcInstructionsKey,
  navigator: Navigator,
  permissionStateFactory: PermissionStateFactory,
  scope: InjektCoroutineScope<KeyUiScope>,
  updateClipboardText: UpdateClipboardTextUseCase
): @Scoped<KeyUiScope> StateFlow<WriteSecureSettingsPcInstructionsModel> = scope.state(
  initial
) {
  timer(200.milliseconds)
    .flatMapLatest { permissionStateFactory(listOf(key.permissionKey)) }
    .filter { it }
    .take(1)
    .onEach { navigator.pop(key) }
    .launchIn(this)
  action(WriteSecureSettingsPcInstructionsModel.copyAdbCommand()) {
    updateClipboardText(state.first().secureSettingsAdbCommand, true)
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
