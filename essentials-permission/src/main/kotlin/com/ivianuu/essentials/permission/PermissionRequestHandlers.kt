package com.ivianuu.essentials.permission

import com.ivianuu.essentials.util.AppCoroutineDispatchers
import com.ivianuu.injekt.Unscoped
import kotlinx.coroutines.withContext

@Unscoped
class PermissionRequestHandlers(
    private val dispatchers: AppCoroutineDispatchers,
    private val manager: PermissionManager,
    requestHandlers: Set<PermissionRequestHandler>
) {

    private val requestHandlers = requestHandlers
        .map { requestHandler ->
            object : PermissionRequestHandler by requestHandler {
                override suspend fun request(permission: Permission) {
                    withContext(dispatchers.default) {
                        requestHandler.request(permission)
                        manager.permissionRequestFinished()
                    }
                }
            }
        }

    fun requestHandlerFor(permission: Permission): PermissionRequestHandler {
        return requestHandlers.firstOrNull { it.handles(permission) }
            ?: error("Couldn't find request handler for $permission")
    }
}
