package com.ivianuu.essentials.permission

import com.ivianuu.injekt.Factory

@Factory
internal class PermissionRequestHandlers(
    @PermissionRequestHandlersSet private val requestHandlers: Set<PermissionRequestHandler>
) {
    fun requestHandlerFor(permission: Permission): PermissionRequestHandler =
        requestHandlers.firstOrNull { it.handles(permission) }
            ?: error("Couldn't find request handler for $permission")
}
