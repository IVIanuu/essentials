/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.permission.writesecuresettings

import androidx.compose.foundation.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.res.*
import com.github.michaelbull.result.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.permission.*
import com.ivianuu.essentials.permission.R
import com.ivianuu.essentials.shell.*
import com.ivianuu.essentials.state.*
import com.ivianuu.essentials.ui.common.*
import com.ivianuu.essentials.ui.material.*
import com.ivianuu.essentials.ui.material.Button
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.essentials.util.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.common.*
import kotlinx.coroutines.flow.*

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
