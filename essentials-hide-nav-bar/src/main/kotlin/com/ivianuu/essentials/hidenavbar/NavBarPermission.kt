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

package com.ivianuu.essentials.hidenavbar

import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.res.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.app.*
import com.ivianuu.essentials.data.*
import com.ivianuu.essentials.permission.*
import com.ivianuu.essentials.permission.writesecuresettings.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.scope.*
import kotlinx.coroutines.flow.*

@Provide class NavBarPermission(private val rp: ResourceProvider) : WriteSecureSettingsPermission {
  override val title: String
    get() = loadResource(R.string.es_permission_nav_bar)
  override val desc: String
    get() = loadResource(R.string.es_permission_nav_bar_desc)
  override val icon: @Composable () -> Unit
    get() = { Icon(painterResource(R.drawable.es_ic_settings), null) }
}

@Provide fun disableHideNavBarWhenPermissionRevokedWorker(
  permissionState: Flow<PermissionState<NavBarPermission>>,
  pref: DataStore<NavBarPrefs>
): ScopeWorker<AppScope> = {
  permissionState
    .filter { !it }
    .collect { pref.updateData { copy(hideNavBar = false) } }
}
