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

import androidx.compose.material.Text
import androidx.compose.ui.res.stringResource
import com.ivianuu.essentials.BuildInfo
import com.ivianuu.essentials.ResourceProvider
import com.ivianuu.essentials.coroutines.timer
import com.ivianuu.essentials.onFailure
import com.ivianuu.essentials.optics.Optics
import com.ivianuu.essentials.permission.PermissionStateFactory
import com.ivianuu.essentials.permission.R
import com.ivianuu.essentials.shell.Shell
import com.ivianuu.essentials.store.action
import com.ivianuu.essentials.store.state
import com.ivianuu.essentials.ui.common.SimpleListScreen
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.KeyUiScope
import com.ivianuu.essentials.ui.navigation.ModelKeyUi
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.util.Toaster
import com.ivianuu.essentials.util.showToast
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.common.TypeKey
import com.ivianuu.injekt.coroutines.NamedCoroutineScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.take
import kotlin.time.milliseconds

data class WriteSecureSettingsKey(
  val permissionKey: TypeKey<WriteSecureSettingsPermission>
) : Key<Boolean>

@Provide val writeSecureSettingsUi: ModelKeyUi<WriteSecureSettingsKey, WriteSecureSettingsModel> = {
  SimpleListScreen(R.string.es_secure_settings_title) {
    item {
      SecureSettingsHeader(
        stringResource(R.string.es_pref_secure_settings_header_summary)
      )
    }
    item {
      ListItem(
        title = { Text(R.string.es_pref_use_pc) },
        subtitle = { Text(R.string.es_pref_use_pc_summary) },
        onClick = model.openPcInstructions
      )
    }
    item {
      ListItem(
        title = { Text(R.string.es_pref_use_root) },
        subtitle = { Text(R.string.es_pref_use_root_summary) },
        onClick = model.grantPermissionsViaRoot
      )
    }
  }
}

@Optics data class WriteSecureSettingsModel(
  val openPcInstructions: () -> Unit = {},
  val grantPermissionsViaRoot: () -> Unit = {}
)

@Provide fun writeSecureSettingsModel(
  buildInfo: BuildInfo,
  key: WriteSecureSettingsKey,
  navigator: Navigator,
  permissionStateFactory: PermissionStateFactory,
  scope: NamedCoroutineScope<KeyUiScope>,
  shell: Shell,
  rp: ResourceProvider,
  toaster: Toaster,
): StateFlow<WriteSecureSettingsModel> = scope.state(
  WriteSecureSettingsModel()
) {
  timer(200.milliseconds)
    .flatMapLatest { permissionStateFactory(listOf(key.permissionKey)) }
    .filter { it }
    .take(1)
    .onEach {
      showToast(R.string.es_secure_settings_permission_granted)
      navigator.pop(key, true)
    }
    .launchIn(this)
  action(WriteSecureSettingsModel.openPcInstructions()) {
    navigator.push(WriteSecureSettingsPcInstructionsKey(key.permissionKey))
  }
  action(WriteSecureSettingsModel.grantPermissionsViaRoot()) {
    shell.run("pm grant ${buildInfo.packageName} android.permission.WRITE_SECURE_SETTINGS")
      .onFailure {
        it.printStackTrace()
        showToast(R.string.es_secure_settings_no_root)
      }
  }
}
