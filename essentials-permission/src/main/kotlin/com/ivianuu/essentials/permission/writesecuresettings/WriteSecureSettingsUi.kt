/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.permission.writesecuresettings

import androidx.compose.foundation.clickable
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.ivianuu.essentials.BuildInfo
import com.ivianuu.essentials.onFailure
import com.ivianuu.essentials.onSuccess
import com.ivianuu.essentials.permission.PermissionStateFactory
import com.ivianuu.essentials.permission.R
import com.ivianuu.essentials.shell.Shell
import com.ivianuu.essentials.state.action
import com.ivianuu.essentials.ui.common.SimpleListScreen
import com.ivianuu.essentials.ui.material.Button
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.KeyUiContext
import com.ivianuu.essentials.ui.navigation.ModelKeyUi
import com.ivianuu.essentials.util.ToastContext
import com.ivianuu.essentials.util.showToast
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.common.TypeKey
import kotlinx.coroutines.flow.first

data class WriteSecureSettingsKey(
  val permissionKey: TypeKey<WriteSecureSettingsPermission>
) : Key<Boolean>

@Provide val writeSecureSettingsUi = ModelKeyUi<WriteSecureSettingsKey, WriteSecureSettingsModel> {
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

@Provide @Composable fun writeSecureSettingsModel(
  buildInfo: BuildInfo,
  permissionStateFactory: PermissionStateFactory,
  shell: Shell,
  T: ToastContext,
  ctx: KeyUiContext<WriteSecureSettingsKey>
) = WriteSecureSettingsModel(
  openPcInstructions = action {
    if (ctx.navigator.push(WriteSecureSettingsPcInstructionsKey(ctx.key.permissionKey)) == true)
      ctx.navigator.pop(ctx.key)
  },
  grantPermissionsViaRoot = action {
    shell.run("pm grant ${buildInfo.packageName} android.permission.WRITE_SECURE_SETTINGS")
      .onSuccess {
        if (permissionStateFactory(listOf(ctx.key.permissionKey)).first()) {
          showToast(R.string.es_secure_settings_permission_granted)
          ctx.navigator.pop(ctx.key)
        }
      }
      .onFailure {
        it.printStackTrace()
        showToast(R.string.es_secure_settings_no_root)
      }
  }
)
