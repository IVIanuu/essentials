/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.permission.writesecuresettings

import androidx.compose.foundation.clickable
import androidx.compose.material.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.ivianuu.essentials.BuildInfo
import com.ivianuu.essentials.Resources
import com.ivianuu.essentials.compose.action
import com.ivianuu.essentials.onFailure
import com.ivianuu.essentials.onSuccess
import com.ivianuu.essentials.permission.PermissionManager
import com.ivianuu.essentials.permission.R
import com.ivianuu.essentials.shell.Shell
import com.ivianuu.essentials.ui.common.SimpleListScreen
import com.ivianuu.essentials.ui.material.Button
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.navigation.CriticalUserFlowKey
import com.ivianuu.essentials.ui.navigation.KeyUiContext
import com.ivianuu.essentials.ui.navigation.Model
import com.ivianuu.essentials.ui.navigation.ModelKeyUi
import com.ivianuu.essentials.ui.navigation.pop
import com.ivianuu.essentials.ui.navigation.push
import com.ivianuu.essentials.util.Toaster
import com.ivianuu.essentials.util.invoke
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.common.TypeKey
import kotlinx.coroutines.flow.first

class WriteSecureSettingsKey(
  val permissionKey: TypeKey<WriteSecureSettingsPermission>
) : CriticalUserFlowKey<Boolean>

@Provide val writeSecureSettingsUi = ModelKeyUi<WriteSecureSettingsKey, WriteSecureSettingsModel> {
  SimpleListScreen(R.string.es_secure_settings_title) {
    item {
      SecureSettingsHeader(
        stringResource(R.string.es_secure_settings_desc)
      )
    }
    item {
      ListItem(
        modifier = Modifier.clickable(onClick = openPcInstructions),
        title = { Text(R.string.es_use_pc) },
        subtitle = { Text(R.string.es_use_pc_summary) },
        trailing = {
          Button(onClick = openPcInstructions) {
            Text(R.string.es_grant)
          }
        }
      )
    }
    item {
      ListItem(
        modifier = Modifier.clickable(onClick = grantPermissionsViaRoot),
        title = { Text(R.string.es_use_root) },
        subtitle = { Text(R.string.es_use_root_summary) },
        trailing = {
          Button(onClick = grantPermissionsViaRoot) {
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
  buildInfo: BuildInfo,
  ctx: KeyUiContext<WriteSecureSettingsKey>,
  permissionManager: PermissionManager,
  resources: Resources,
  shell: Shell,
  toaster: Toaster
) = Model {
  WriteSecureSettingsModel(
    openPcInstructions = action {
      if (ctx.navigator.push(WriteSecureSettingsPcInstructionsKey(ctx.key.permissionKey)) == true)
        ctx.navigator.pop(ctx.key)
    },
    grantPermissionsViaRoot = action {
      shell.run("pm grant ${buildInfo.packageName} android.permission.WRITE_SECURE_SETTINGS")
        .onSuccess {
          if (permissionManager.permissionState(listOf(ctx.key.permissionKey)).first()) {
            toaster(R.string.es_secure_settings_permission_granted)
            ctx.navigator.pop(ctx.key)
          }
        }
        .onFailure {
          it.printStackTrace()
          toaster(R.string.es_secure_settings_no_root)
        }
    }
  )
}
