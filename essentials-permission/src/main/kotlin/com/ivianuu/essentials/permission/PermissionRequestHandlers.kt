package com.ivianuu.essentials.permission

import com.ivianuu.injekt.Transient

@Transient
internal class PermissionRequestHandlers(
    private val manager: PermissionManager,
    requestHandlers: Set<PermissionRequestHandler>
) {

    private val requestHandlers = requestHandlers
        .map { requestHandler ->
            object : PermissionRequestHandler by requestHandler {
                override suspend fun request(permission: Permission) {
                    requestHandler.request(permission)
                    manager.permissionRequestFinished()
                }
            }
        }

    fun requestHandlerFor(permission: Permission): PermissionRequestHandler {
        return requestHandlers.firstOrNull { it.handles(permission) }
            ?: error("Couldn't find request handler for $permission")
    }
}
