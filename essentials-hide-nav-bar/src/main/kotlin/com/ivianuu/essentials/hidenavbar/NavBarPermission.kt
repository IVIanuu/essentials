/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.hidenavbar

import androidx.compose.material.Icon
import com.ivianuu.essentials.Resources
import com.ivianuu.essentials.app.ScopeWorker
import com.ivianuu.essentials.data.DataStore
import com.ivianuu.essentials.permission.Permission
import com.ivianuu.essentials.permission.PermissionManager
import com.ivianuu.essentials.permission.writesecuresettings.WriteSecureSettingsPermission
import com.ivianuu.essentials.ui.UiScope
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.common.typeKeyOf
import kotlinx.coroutines.flow.first

@Provide class NavBarPermission(resources: Resources) : WriteSecureSettingsPermission(
  title = resources(R.string.es_permission_nav_bar),
  desc = resources(R.string.es_permission_nav_bar_desc),
  icon = Permission.Icon { Icon(R.drawable.es_ic_settings) }
)

@Provide fun disableHideNavBarWhenPermissionRevokedWorker(
  navBarFeatureSupported: NavBarFeatureSupported,
  permissionManager: PermissionManager,
  pref: DataStore<NavBarPrefs>
) = ScopeWorker<UiScope> {
  if (navBarFeatureSupported.value &&
    !permissionManager.permissionState(listOf(typeKeyOf<NavBarPermission>())).first()
  )
    pref.updateData { copy(hideNavBar = false) }
}
