package com.ivianuu.essentials.hidenavbar

import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import com.ivianuu.essentials.android.prefs.Pref
import com.ivianuu.essentials.app.ScopeWorker
import com.ivianuu.essentials.permission.PermissionBinding
import com.ivianuu.essentials.permission.PermissionState
import com.ivianuu.essentials.permission.writesecuresettings.WriteSecureSettingsPermission
import com.ivianuu.essentials.util.ResourceProvider
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.scope.AppGivenScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter

@PermissionBinding
@Given
class NavBarPermission(
    @Given private val resourceProvider: ResourceProvider
) : WriteSecureSettingsPermission {
    override val title: String = resourceProvider.string(R.string.es_permission_nav_bar)
    override val desc: String = resourceProvider.string(R.string.es_permission_nav_bar_desc)
    override val icon: @Composable () -> Unit = {
        Icon(painterResource(R.drawable.es_ic_settings), null)
    }
}

@Given
fun disableHideNavBarWhenPermissionRevokedWorker(
    @Given permissionState: Flow<PermissionState<NavBarPermission>>,
    @Given pref: Pref<NavBarPrefs>
): ScopeWorker<AppGivenScope> = {
    permissionState
        .filter { !it }
        .collect { pref.update { copy(hideNavBar = false) } }
}
