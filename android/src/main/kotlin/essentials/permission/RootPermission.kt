/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.permission

import androidx.compose.runtime.*
import essentials.shell.*
import essentials.util.*
import injekt.*

abstract class RootPermission(
  override val title: String,
  override val desc: String? = null,
  override val icon: (@Composable () -> Unit)? = null
) : Permission {
  @Provide companion object {
    @Provide fun <P : RootPermission> stateProvider(shell: Shell) =
      PermissionStateProvider<P> { shell.isAvailable() }

    @Provide fun <P : RootPermission> requestHandler(
      shell: Shell,
      toaster: Toaster
    ) = PermissionRequestHandler<P> {
      if (!shell.isAvailable()) toaster.toast("Your device is not rooted!")
    }
  }
}
