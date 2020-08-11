package com.ivianuu.essentials.permission.writesecuresettings

import com.ivianuu.essentials.permission.GivenPermissionRequestHandler
import com.ivianuu.essentials.permission.GivenPermissionStateProvider
import com.ivianuu.essentials.permission.KeyWithValue
import com.ivianuu.essentials.permission.Permission
import com.ivianuu.essentials.permission.PermissionRequestHandler
import com.ivianuu.essentials.permission.PermissionStateProvider
import com.ivianuu.essentials.permission.withValue
import com.ivianuu.essentials.securesettings.SecureSettings
import com.ivianuu.essentials.securesettings.SecureSettingsPage
import com.ivianuu.essentials.ui.navigation.navigator

fun WriteSecureSettingsPermission(vararg metadata: KeyWithValue<*>) = Permission(
    Permission.IsWriteSecureSettingsPermission withValue Unit,
    *metadata
)

val Permission.Companion.IsWriteSecureSettingsPermission by lazy {
    Permission.Key<Unit>("IsWriteSecureSettingsPermission")
}

@GivenPermissionStateProvider
internal class WriteSecureSettingsPermissionStateProvider : PermissionStateProvider {

    override fun handles(permission: Permission): Boolean =
        Permission.IsWriteSecureSettingsPermission in permission

    override suspend fun isGranted(permission: Permission): Boolean =
        SecureSettings.canWrite()

}

@GivenPermissionRequestHandler
internal class WriteSecureSettingsPermissionRequestHandler : PermissionRequestHandler {

    override fun handles(permission: Permission): Boolean =
        Permission.IsWriteSecureSettingsPermission in permission

    override suspend fun request(permission: Permission) {
        navigator.push<Boolean> { SecureSettingsPage() }
    }

}
