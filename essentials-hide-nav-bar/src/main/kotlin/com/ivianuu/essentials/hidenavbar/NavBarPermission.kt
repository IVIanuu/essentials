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

@Provide class NavBarPermission(
  private val stringResource: StringResourceProvider
) : WriteSecureSettingsPermission {
  override val title: String = stringResource(R.string.es_permission_nav_bar, emptyList())
  override val desc: String = stringResource(R.string.es_permission_nav_bar_desc, emptyList())
  override val icon: @Composable () -> Unit = {
    Icon(painterResource(R.drawable.es_ic_settings), null)
  }
}

@Provide fun disableHideNavBarWhenPermissionRevokedWorker(
  permissionState: Flow<PermissionState<NavBarPermission>>,
  pref: DataStore<NavBarPrefs>
): ScopeWorker<AppScope> = {
  permissionState
    .filter { !it }
    .collect { pref.updateData { copy(hideNavBar = false) } }
}
