package com.ivianuu.essentials.gestures.action

import com.github.ajalt.timberkt.d
import com.ivianuu.essentials.permission.PermissionManager
import com.ivianuu.essentials.unlock.ScreenUnlocker
import com.ivianuu.essentials.util.AppDispatchers
import com.ivianuu.injekt.Factory
import kotlinx.coroutines.withContext

@Factory
class ActionExecutors(
    private val actionStore: ActionStore,
    private val dispatchers: AppDispatchers,
    private val permissionManager: PermissionManager,
    private val screenUnlocker: ScreenUnlocker
) {

    suspend fun execute(key: String) = withContext(dispatchers.default) {
        d { "execute $key" }
        val action = actionStore.getAction(key)

        // check permissions
        if (!permissionManager.request(action.permissions)) {
            d { "couldn't get permissions for $key" }
            return@withContext
        }

        // unlock screen
        if (action.unlockScreen && !screenUnlocker.unlockScreen()) {
            d { "couldn't unlock screen for $key" }
            return@withContext
        }

        d { "fire $key" }

        // fire
        try {
            action.executor()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}