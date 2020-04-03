package com.ivianuu.essentials.permission.root

import androidx.fragment.app.FragmentActivity
import com.ivianuu.essentials.permission.MetaDataKeyWithValue
import com.ivianuu.essentials.permission.Metadata
import com.ivianuu.essentials.permission.Permission
import com.ivianuu.essentials.permission.PermissionManager
import com.ivianuu.essentials.permission.PermissionRequestHandler
import com.ivianuu.essentials.permission.PermissionResult
import com.ivianuu.essentials.permission.PermissionStateProvider
import com.ivianuu.essentials.permission.R
import com.ivianuu.essentials.permission.bindPermissionRequestHandlerIntoSet
import com.ivianuu.essentials.permission.bindPermissionStateProviderIntoSet
import com.ivianuu.essentials.permission.metadataOf
import com.ivianuu.essentials.permission.withValue
import com.ivianuu.essentials.shell.Shell
import com.ivianuu.essentials.util.Toaster
import com.ivianuu.injekt.ApplicationScope
import com.ivianuu.injekt.ComponentBuilder
import com.ivianuu.injekt.Factory
import com.ivianuu.injekt.Module

fun RootPermission(vararg metadata: MetaDataKeyWithValue<*>) = Permission(
    metadata = metadataOf(
        Metadata.IsRootPermission withValue Unit,
        *metadata
    )
)

@ApplicationScope
@Module
private fun ComponentBuilder.rootPermission() {
    bindPermissionStateProviderIntoSet<RootPermissionStateProvider>()
    bindPermissionRequestHandlerIntoSet<RootPermissionRequestHandler>()
}

val Metadata.Companion.IsRootPermission by lazy {
    Metadata.Key<Unit>("IsRootPermission")
}

@Factory
private class RootPermissionStateProvider(private val shell: Shell) : PermissionStateProvider {

    override fun handles(permission: Permission): Boolean =
        Metadata.IsRootPermission in permission.metadata

    override suspend fun isGranted(permission: Permission): Boolean = shell.isAvailable()
}

@Factory
private class RootPermissionRequestHandler(
    private val shell: Shell,
    private val toaster: Toaster
) : PermissionRequestHandler {
    override fun handles(permission: Permission): Boolean =
        Metadata.IsRootPermission in permission.metadata

    override suspend fun request(
        activity: FragmentActivity,
        manager: PermissionManager,
        permission: Permission
    ): PermissionResult {
        val isOk = shell.isAvailable()
        if (!isOk) toaster.toast(R.string.es_no_root)
        return PermissionResult(isOk)
    }
}
