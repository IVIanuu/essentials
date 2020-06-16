package com.ivianuu.essentials.permission.writesecuresettings

import com.ivianuu.essentials.permission.BindPermissionRequestHandler
import com.ivianuu.essentials.permission.BindPermissionStateProvider
import com.ivianuu.essentials.permission.KeyWithValue
import com.ivianuu.essentials.permission.Permission
import com.ivianuu.essentials.permission.PermissionRequestHandler
import com.ivianuu.essentials.permission.PermissionStateProvider
import com.ivianuu.essentials.permission.withValue
import com.ivianuu.essentials.securesettings.SecureSettingsHelper
import com.ivianuu.essentials.securesettings.SecureSettingsPage
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.injekt.Transient

fun WriteSecureSettingsPermission(vararg metadata: KeyWithValue<*>) = Permission(
    Permission.IsWriteSecureSettingsPermission withValue Unit,
    *metadata
)

val Permission.Companion.IsWriteSecureSettingsPermission by lazy {
    Permission.Key<Unit>("IsWriteSecureSettingsPermission")
}

@BindPermissionStateProvider
@Transient
internal class WriteSecureSettingsPermissionStateProvider(
    private val secureSettingsHelper: SecureSettingsHelper
) : PermissionStateProvider {

    override fun handles(permission: Permission): Boolean =
        Permission.IsWriteSecureSettingsPermission in permission

    override suspend fun isGranted(permission: Permission): Boolean =
        secureSettingsHelper.canWriteSecureSettings()
}

@BindPermissionRequestHandler
@Transient
internal class WriteSecureSettingsPermissionRequestHandler(
    private val navigator: Navigator,
    private val secureSettingsPage: SecureSettingsPage
) : PermissionRequestHandler {

    override fun handles(permission: Permission): Boolean =
        Permission.IsWriteSecureSettingsPermission in permission

    override suspend fun request(permission: Permission) {
        navigator.push<Boolean> { secureSettingsPage() }.await()
    }

}
