package com.ivianuu.essentials.permission

import com.ivianuu.essentials.util.AppCoroutineDispatchers
import com.ivianuu.essentials.util.dispatchers
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.Reader
import com.ivianuu.injekt.given
import kotlinx.coroutines.withContext

@Given
@Reader
class PermissionRequestHandlers {

    private val requestHandlers = given<Set<PermissionRequestHandler>>()
        .map { requestHandler ->
            object : PermissionRequestHandler by requestHandler {
                override suspend fun request(permission: Permission) {
                    withContext(dispatchers.default) {
                        requestHandler.request(permission)
                        given<PermissionManager>().permissionRequestFinished()
                    }
                }
            }
        }

    fun requestHandlerFor(permission: Permission): PermissionRequestHandler {
        return requestHandlers.firstOrNull { it.handles(permission) }
            ?: error("Couldn't find request handler for $permission")
    }
}
