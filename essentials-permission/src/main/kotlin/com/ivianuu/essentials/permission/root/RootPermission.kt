package com.ivianuu.essentials.permission.root

import com.ivianuu.essentials.permission.GivenPermissionRequestHandler
import com.ivianuu.essentials.permission.GivenPermissionStateProvider
import com.ivianuu.essentials.permission.KeyWithValue
import com.ivianuu.essentials.permission.Permission
import com.ivianuu.essentials.permission.PermissionRequestHandler
import com.ivianuu.essentials.permission.PermissionStateProvider
import com.ivianuu.essentials.permission.R
import com.ivianuu.essentials.permission.withValue
import com.ivianuu.essentials.shell.Shell
import com.ivianuu.essentials.util.Toaster

fun RootPermission(vararg metadata: KeyWithValue<*>) = Permission(
    Permission.IsRootPermission withValue Unit,
    *metadata
)

val Permission.Companion.IsRootPermission by lazy {
    Permission.Key<Unit>("IsRootPermission")
}

@GivenPermissionStateProvider
internal class RootPermissionStateProvider : PermissionStateProvider {

    override fun handles(permission: Permission): Boolean =
        Permission.IsRootPermission in permission

    override suspend fun isGranted(permission: Permission): Boolean = Shell.isAvailable()
}

@GivenPermissionRequestHandler
internal class RootPermissionRequestHandler : PermissionRequestHandler {
    override fun handles(permission: Permission): Boolean =
        Permission.IsRootPermission in permission

    override suspend fun request(permission: Permission) {
        val isOk = Shell.isAvailable()
        if (!isOk) Toaster.toast(R.string.es_no_root)
    }
}
