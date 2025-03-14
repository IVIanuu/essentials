/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.permission

import androidx.compose.runtime.*
import essentials.*
import essentials.shell.*
import essentials.util.*
import injekt.*

abstract class RootPermission(
  override val title: String,
  override val desc: String? = null,
  override val icon: (@Composable () -> Unit)? = null
) : Permission {
  @Provide companion object {
    @Provide suspend fun <P : RootPermission> state(
      scope: Scope<*> = inject
    ): PermissionState<P> = isShellAvailable()

    @Provide suspend fun <P : RootPermission> request(
      scope: Scope<*> = inject
    ): PermissionRequestResult<P> {
      if (!isShellAvailable())
        showToast("Your device is not rooted!")
    }
  }
}
