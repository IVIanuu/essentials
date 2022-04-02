/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.hidenavbar

import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import com.ivianuu.essentials.ResourceProvider
import com.ivianuu.essentials.app.ScopeWorker
import com.ivianuu.essentials.data.DataStore
import com.ivianuu.essentials.loadResource
import com.ivianuu.essentials.permission.PermissionState
import com.ivianuu.essentials.permission.writesecuresettings.WriteSecureSettingsPermission
import com.ivianuu.essentials.ui.UiScope
import com.ivianuu.injekt.Provide
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

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
