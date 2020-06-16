package com.ivianuu.essentials.permission.root

import com.ivianuu.essentials.permission.BindPermissionRequestHandler
import com.ivianuu.essentials.permission.BindPermissionStateProvider
import com.ivianuu.essentials.permission.MetaDataKeyWithValue
import com.ivianuu.essentials.permission.Metadata
import com.ivianuu.essentials.permission.Permission
import com.ivianuu.essentials.permission.PermissionRequestHandler
import com.ivianuu.essentials.permission.PermissionStateProvider
import com.ivianuu.essentials.permission.R
import com.ivianuu.essentials.permission.metadataOf
import com.ivianuu.essentials.permission.withValue
import com.ivianuu.essentials.shell.Shell
import com.ivianuu.essentials.util.Toaster
import com.ivianuu.injekt.Transient

fun RootPermission(vararg metadata: MetaDataKeyWithValue<*>) = Permission(
    metadata = metadataOf(
        Metadata.IsRootPermission withValue Unit,
        *metadata
    )
)

val Metadata.Companion.IsRootPermission by lazy {
    Metadata.Key<Unit>("IsRootPermission")
}

@BindPermissionStateProvider
@Transient
internal class RootPermissionStateProvider(private val shell: Shell) : PermissionStateProvider {

    override fun handles(permission: Permission): Boolean =
        Metadata.IsRootPermission in permission.metadata

    override suspend fun isGranted(permission: Permission): Boolean = shell.isAvailable()
}

@BindPermissionRequestHandler
@Transient
internal class RootPermissionRequestHandler(
    private val shell: Shell,
    private val toaster: Toaster
) : PermissionRequestHandler {
    override fun handles(permission: Permission): Boolean =
        Metadata.IsRootPermission in permission.metadata

    override suspend fun request(permission: Permission) {
        val isOk = shell.isAvailable()
        if (!isOk) toaster.toast(R.string.es_no_root)
    }
}
