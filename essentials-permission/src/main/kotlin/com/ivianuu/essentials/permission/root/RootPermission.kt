/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.permission.root

import com.ivianuu.essentials.Resources
import com.ivianuu.essentials.permission.Permission
import com.ivianuu.essentials.permission.PermissionRequestHandler
import com.ivianuu.essentials.permission.PermissionStateProvider
import com.ivianuu.essentials.permission.R
import com.ivianuu.essentials.shell.Shell
import com.ivianuu.essentials.util.Toaster
import com.ivianuu.essentials.util.invoke
import com.ivianuu.injekt.Provide

abstract class RootPermission(
  override val title: String,
  override val desc: String? = null,
  override val icon: Permission.Icon? = null
) : Permission

@Provide fun <P : RootPermission> rootPermissionStateProvider(shell: Shell) =
  PermissionStateProvider<P> { shell.isAvailable() }

@Provide fun <P : RootPermission> rootPermissionRequestHandler(
  shell: Shell,
  resources: Resources,
  toaster: Toaster
) = PermissionRequestHandler<P> {
  if (!shell.isAvailable()) toaster(R.string.es_no_root)
}
