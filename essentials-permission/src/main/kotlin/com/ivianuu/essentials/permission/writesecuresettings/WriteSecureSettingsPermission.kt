package com.ivianuu.essentials.permission.writesecuresettings

import com.ivianuu.essentials.permission.KeyWithValue
import com.ivianuu.essentials.permission.Permission
import com.ivianuu.essentials.permission.PermissionRequestHandler
import com.ivianuu.essentials.permission.PermissionRequestHandlerBinding
import com.ivianuu.essentials.permission.PermissionStateProvider
import com.ivianuu.essentials.permission.PermissionStateProviderBinding
import com.ivianuu.essentials.permission.withValue
import com.ivianuu.essentials.securesettings.SecureSettingsPage
import com.ivianuu.essentials.securesettings.hasSecureSettingsPermission
import com.ivianuu.essentials.ui.navigation.Navigator

fun WriteSecureSettingsPermission(vararg metadata: KeyWithValue<*>) = Permission(
    Permission.IsWriteSecureSettingsPermission withValue Unit,
    *metadata
)

val Permission.Companion.IsWriteSecureSettingsPermission by lazy {
    Permission.Key<Unit>("IsWriteSecureSettingsPermission")
}

@PermissionStateProviderBinding
class WriteSecureSettingsPermissionStateProvider(
    private val hasSecureSettingsPermission: hasSecureSettingsPermission,
) : PermissionStateProvider {

    override fun handles(permission: Permission): Boolean =
        Permission.IsWriteSecureSettingsPermission in permission

    override suspend fun isGranted(permission: Permission): Boolean =
        hasSecureSettingsPermission()

}

@PermissionRequestHandlerBinding
class WriteSecureSettingsPermissionRequestHandler(
    private val navigator: Navigator,
    private val secureSettingsPage: SecureSettingsPage,
) : PermissionRequestHandler {

    override fun handles(permission: Permission): Boolean =
        Permission.IsWriteSecureSettingsPermission in permission

    override suspend fun request(permission: Permission) {
        navigator.push<Boolean> { secureSettingsPage(false) }
    }

}
