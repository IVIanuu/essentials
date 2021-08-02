/*
 * Copyright 2021 Manuel Wrage
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ivianuu.essentials.permission.root

import com.ivianuu.essentials.*
import com.ivianuu.essentials.permission.*
import com.ivianuu.essentials.permission.R
import com.ivianuu.essentials.shell.*
import com.ivianuu.essentials.util.*
import com.ivianuu.injekt.*

interface RootPermission : Permission

@Provide fun <P : RootPermission> rootPermissionStateProvider(
  isShellAvailable: IsShellAvailableUseCase
): PermissionStateProvider<P> = { isShellAvailable() }

@Provide fun <P : RootPermission> rootPermissionRequestHandler(
  isShellAvailable: IsShellAvailableUseCase,
  rp: ResourceProvider,
  toaster: Toaster
): PermissionRequestHandler<P> = {
  if (!isShellAvailable())
    showToast(R.string.es_no_root)
}
