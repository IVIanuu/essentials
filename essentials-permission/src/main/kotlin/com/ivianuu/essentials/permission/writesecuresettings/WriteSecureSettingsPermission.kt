package com.ivianuu.essentials.permission.writesecuresettings

import androidx.fragment.app.FragmentActivity
import com.ivianuu.essentials.permission.MetaDataKeyWithValue
import com.ivianuu.essentials.permission.Metadata
import com.ivianuu.essentials.permission.Permission
import com.ivianuu.essentials.permission.PermissionManager
import com.ivianuu.essentials.permission.PermissionRequestHandler
import com.ivianuu.essentials.permission.PermissionResult
import com.ivianuu.essentials.permission.PermissionStateProvider
import com.ivianuu.essentials.permission.bindPermissionRequestHandler
import com.ivianuu.essentials.permission.bindPermissionStateProvider
import com.ivianuu.essentials.permission.metadataOf
import com.ivianuu.essentials.permission.with
import com.ivianuu.essentials.securesettings.SecureSettingsHelper
import com.ivianuu.essentials.securesettings.SecureSettingsRoute
import com.ivianuu.essentials.ui.navigation.NavigatorState
import com.ivianuu.injekt.Factory
import com.ivianuu.injekt.Module

fun WriteSecureSettingsPermission(vararg metadata: MetaDataKeyWithValue<*>) = Permission(
    metadata = metadataOf(
        Metadata.IsWriteSecureSettingsPermission with Unit,
        *metadata
    )
)

val Metadata.Companion.IsWriteSecureSettingsPermission by lazy {
    Metadata.Key<Unit>("IsWriteSecureSettingsPermission")
}

internal val EsWriteSecureSettingsPermissionModule = Module {
    bindPermissionRequestHandler<WriteSecureSettingsPermissionRequestHandler>()
    bindPermissionStateProvider<WriteSecureSettingsPermissionStateProvider>()
}

@Factory
internal class WriteSecureSettingsPermissionStateProvider(
    private val secureSettingsHelper: SecureSettingsHelper
) : PermissionStateProvider {

    override fun handles(permission: Permission): Boolean =
        Metadata.IsWriteSecureSettingsPermission in permission.metadata

    override suspend fun isGranted(permission: Permission): Boolean =
        secureSettingsHelper.canWriteSecureSettings()
}

@Factory
internal class WriteSecureSettingsPermissionRequestHandler(
    private val navigator: NavigatorState
) : PermissionRequestHandler {
    override fun handles(permission: Permission): Boolean =
        Metadata.IsWriteSecureSettingsPermission in permission.metadata

    override suspend fun request(
        activity: FragmentActivity,
        manager: PermissionManager,
        permission: Permission
    ): PermissionResult {
        return PermissionResult(navigator.push<Boolean>(SecureSettingsRoute()) ?: false)
    }
}
