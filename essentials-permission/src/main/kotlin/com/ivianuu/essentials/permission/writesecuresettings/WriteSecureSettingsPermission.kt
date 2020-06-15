package com.ivianuu.essentials.permission.writesecuresettings

import androidx.fragment.app.FragmentActivity
import com.ivianuu.essentials.permission.BindPermissionRequestHandler
import com.ivianuu.essentials.permission.BindPermissionStateProvider
import com.ivianuu.essentials.permission.MetaDataKeyWithValue
import com.ivianuu.essentials.permission.Metadata
import com.ivianuu.essentials.permission.Permission
import com.ivianuu.essentials.permission.PermissionManager
import com.ivianuu.essentials.permission.PermissionRequestHandler
import com.ivianuu.essentials.permission.PermissionResult
import com.ivianuu.essentials.permission.PermissionStateProvider
import com.ivianuu.essentials.permission.metadataOf
import com.ivianuu.essentials.permission.withValue
import com.ivianuu.essentials.securesettings.SecureSettingsHelper
import com.ivianuu.essentials.securesettings.SecureSettingsPage
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.injekt.Transient

fun WriteSecureSettingsPermission(vararg metadata: MetaDataKeyWithValue<*>) = Permission(
    metadata = metadataOf(
        Metadata.IsWriteSecureSettingsPermission withValue Unit,
        *metadata
    )
)

val Metadata.Companion.IsWriteSecureSettingsPermission by lazy {
    Metadata.Key<Unit>("IsWriteSecureSettingsPermission")
}

@BindPermissionStateProvider
@Transient
internal class WriteSecureSettingsPermissionStateProvider(
    private val secureSettingsHelper: SecureSettingsHelper
) : PermissionStateProvider {

    override fun handles(permission: Permission): Boolean =
        Metadata.IsWriteSecureSettingsPermission in permission.metadata

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
        Metadata.IsWriteSecureSettingsPermission in permission.metadata

    override suspend fun request(
        activity: FragmentActivity,
        manager: PermissionManager,
        permission: Permission
    ): PermissionResult {
        return PermissionResult(navigator.push<Boolean> { secureSettingsPage() }.await() ?: false)
    }
}
