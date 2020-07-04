package com.ivianuu.essentials.permission.root

import com.ivianuu.essentials.permission.BindPermissionRequestHandler
import com.ivianuu.essentials.permission.BindPermissionStateProvider
import com.ivianuu.essentials.permission.KeyWithValue
import com.ivianuu.essentials.permission.Permission
import com.ivianuu.essentials.permission.PermissionRequestHandler
import com.ivianuu.essentials.permission.PermissionStateProvider
import com.ivianuu.essentials.permission.R
import com.ivianuu.essentials.permission.withValue
import com.ivianuu.essentials.shell.Shell
import com.ivianuu.essentials.util.Toaster
import com.ivianuu.injekt.Unscoped

fun RootPermission(vararg metadata: KeyWithValue<*>) = Permission(
    Permission.IsRootPermission withValue Unit,
    *metadata
)

val Permission.Companion.IsRootPermission by lazy {
    Permission.Key<Unit>("IsRootPermission")
}

@BindPermissionStateProvider
@Unscoped
internal class RootPermissionStateProvider(private val shell: Shell) : PermissionStateProvider {

    override fun handles(permission: Permission): Boolean =
        Permission.IsRootPermission in permission

    override suspend fun isGranted(permission: Permission): Boolean = shell.isAvailable()
}

@BindPermissionRequestHandler
@Unscoped
internal class RootPermissionRequestHandler(
    private val shell: Shell,
    private val toaster: Toaster
) : PermissionRequestHandler {
    override fun handles(permission: Permission): Boolean =
        Permission.IsRootPermission in permission

    override suspend fun request(permission: Permission) {
        val isOk = shell.isAvailable()
        if (!isOk) toaster.toast(R.string.es_no_root)
    }
}
