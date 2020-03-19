package com.ivianuu.essentials.permission

import com.ivianuu.injekt.Factory

@Factory
internal class PermissionRequestHandlers(
    @PermissionRequestHandlersSet private val requestHandlers: Set<PermissionRequestHandler>
) {
    fun requestHandlerFor(permission: Permission): PermissionRequestHandler =
        requestHandlers.first { it.handles(permission) }
}
