/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.permission.writesecuresettings

import androidx.compose.foundation.clickable
import androidx.compose.material.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.ivianuu.essentials.AppConfig
import com.ivianuu.essentials.compose.action
import com.ivianuu.essentials.onFailure
import com.ivianuu.essentials.onSuccess
import com.ivianuu.essentials.permission.PermissionManager
import com.ivianuu.essentials.permission.R
import com.ivianuu.essentials.shell.Shell
import com.ivianuu.essentials.ui.common.SimpleListScreen
import com.ivianuu.essentials.ui.material.Button
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.navigation.CriticalUserFlowScreen
import com.ivianuu.essentials.ui.navigation.Model
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.ui.navigation.Ui
import com.ivianuu.essentials.ui.navigation.pop
import com.ivianuu.essentials.ui.navigation.push
import com.ivianuu.essentials.util.Toaster
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.common.TypeKey
import kotlinx.coroutines.flow.first

class WriteSecureSettingsScreen(
  val permissionKey: TypeKey<WriteSecureSettingsPermission>
) : CriticalUserFlowScreen<Boolean>

@Provide val writeSecureSettingsUi =
  Ui<WriteSecureSettingsScreen, WriteSecureSettingsModel> { model ->
    SimpleListScreen(R.string.es_secure_settings_title) {
      item {
        SecureSettingsHeader(
          stringResource(R.string.es_secure_settings_desc)
        )
      }
      item {
        ListItem(
          modifier = Modifier.clickable(onClick = model.openPcInstructions),
          title = { Text(R.string.es_use_pc) },
          subtitle = { Text(R.string.es_use_pc_summary) },
          trailing = {
            Button(onClick = model.openPcInstructions) {
              Text(R.string.es_grant)
            }
          }
        )
    }
    item {
      ListItem(
        modifier = Modifier.clickable(onClick = model.grantPermissionsViaRoot),
        title = { Text(R.string.es_use_root) },
        subtitle = { Text(R.string.es_use_root_summary) },
        trailing = {
          Button(onClick = model.grantPermissionsViaRoot) {
            Text(R.string.es_grant)
          }
        }
      )
    }
  }
}

data class WriteSecureSettingsModel(
  val openPcInstructions: () -> Unit,
  val grantPermissionsViaRoot: () -> Unit
)

@Provide fun writeSecureSettingsModel(
  appConfig: AppConfig,
  navigator: Navigator,
  permissionManager: PermissionManager,
  screen: WriteSecureSettingsScreen,
  shell: Shell,
  toaster: Toaster
) = Model {
  WriteSecureSettingsModel(
    openPcInstructions = action {
      if (navigator.push(WriteSecureSettingsPcInstructionsScreen(screen.permissionKey)) == true)
        navigator.pop(screen)
    },
    grantPermissionsViaRoot = action {
      shell.run("pm grant ${appConfig.packageName} android.permission.WRITE_SECURE_SETTINGS")
        .onSuccess {
          if (permissionManager.permissionState(listOf(screen.permissionKey)).first()) {
            toaster(R.string.es_secure_settings_permission_granted)
            navigator.pop(screen)
          }
        }
        .onFailure {
          it.printStackTrace()
          toaster(R.string.es_secure_settings_no_root)
        }
    }
  )
}
