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

@Provide class NavBarPermission(private val _: ResourceProvider) : WriteSecureSettingsPermission {
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
