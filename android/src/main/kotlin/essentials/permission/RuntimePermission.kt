/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.permission

import android.app.*
import android.content.pm.*
import androidx.activity.result.contract.*
import androidx.compose.runtime.*
import essentials.ui.navigation.*
import injekt.*

abstract class RuntimePermission(
  val permissionName: String,
  override val title: String,
  override val desc: String? = null,
  override val icon: (@Composable () -> Unit)? = null
) : Permission {
  @Provide companion object {
    @Provide fun <P : RuntimePermission> state(
      permission: P,
      context: Application
    ): PermissionState<P> =
      context.checkSelfPermission(permission.permissionName) ==
          PackageManager.PERMISSION_GRANTED

    @Provide suspend fun <P : RuntimePermission> request(
      permission: P,
      context: Application,
      navigator: Navigator
    ): PermissionRequestResult<P> {
      val contract = ActivityResultContracts.RequestPermission()
      val intent = contract.createIntent(context, permission.permissionName)
      navigator.push(intent.asScreen())
    }
  }
}
