/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.hidenavbar

import androidx.compose.material.*
import androidx.compose.runtime.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.app.*
import com.ivianuu.essentials.data.*
import com.ivianuu.essentials.permission.*
import com.ivianuu.essentials.permission.writesecuresettings.*
import com.ivianuu.essentials.ui.*
import com.ivianuu.injekt.*
import kotlinx.coroutines.flow.*

@Provide class NavBarPermission(private val RP: ResourceProvider) : WriteSecureSettingsPermission {
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
) = ScopeWorker<UiScope> {
  if (navBarFeatureSupported.value && !permissionState.first())
    pref.updateData { copy(hideNavBar = false) }
}
