/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.permission.root

import com.ivianuu.essentials.permission.*
import com.ivianuu.essentials.permission.R
import com.ivianuu.essentials.shell.*
import com.ivianuu.essentials.util.*

interface RootPermission : Permission

@Provide fun <P : RootPermission> rootPermissionStateProvider(
  shell: Shell
) = PermissionStateProvider<P> { shell.isAvailable() }

@Provide fun <P : RootPermission> rootPermissionRequestHandler(
  shell: Shell,
  T: ToastContext
) = PermissionRequestHandler<P> {
  if (!shell.isAvailable()) showToast(R.string.es_no_root)
}
