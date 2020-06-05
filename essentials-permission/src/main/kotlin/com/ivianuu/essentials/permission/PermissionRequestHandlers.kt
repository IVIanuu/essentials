package com.ivianuu.essentials.permission

import com.ivianuu.injekt.Transient

@Transient
internal class PermissionRequestHandlers(
    private val requestHandlers: Set<PermissionRequestHandler>
) {
    fun requestHandlerFor(permission: Permission): PermissionRequestHandler =
        requestHandlers.firstOrNull { it.handles(permission) }
            ?: error("Couldn't find request handler for $permission")
}
