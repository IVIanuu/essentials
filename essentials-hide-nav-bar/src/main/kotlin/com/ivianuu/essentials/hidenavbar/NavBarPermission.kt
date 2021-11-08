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

import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import com.ivianuu.essentials.Res
import com.ivianuu.essentials.app.ScopeWorker
import com.ivianuu.essentials.data.DataStore
import com.ivianuu.essentials.loadResource
import com.ivianuu.essentials.permission.PermissionState
import com.ivianuu.essentials.permission.writesecuresettings.WriteSecureSettingsPermission
import com.ivianuu.essentials.ui.UiComponent
import com.ivianuu.injekt.Provide
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

@Provide @Res class NavBarPermission : WriteSecureSettingsPermission {
  override val title: String
    get() = loadResource(R.string.es_permission_nav_bar)
  override val desc: String
    get() = loadResource(R.string.es_permission_nav_bar_desc)
  override val icon: @Composable () -> Unit
    get() = { Icon(R.drawable.es_ic_settings) }
}

@Provide fun disableHideNavBarWhenPermissionRevokedWorker(
  navBarFeatureSupported: NavBarFeatureSupported,
  permissionState: Flow<PermissionState<NavBarPermission>>,
  pref: DataStore<NavBarPrefs>
): ScopeWorker<UiComponent> = {
  if (navBarFeatureSupported.value && !permissionState.first())
    pref.updateData { copy(hideNavBar = false) }
}
