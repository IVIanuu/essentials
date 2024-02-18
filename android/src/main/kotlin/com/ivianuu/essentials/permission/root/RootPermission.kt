/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.permission.root

import androidx.compose.runtime.*
import com.ivianuu.essentials.android.*
import com.ivianuu.essentials.android.R
import com.ivianuu.essentials.permission.*
import com.ivianuu.essentials.shell.*
import com.ivianuu.essentials.util.*
import com.ivianuu.injekt.*

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
      if (!shell.isAvailable()) toaster("Your device is not rooted!")
    }
  }
}
