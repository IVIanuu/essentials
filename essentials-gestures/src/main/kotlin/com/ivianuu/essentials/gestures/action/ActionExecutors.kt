package com.ivianuu.essentials.gestures.action

import com.ivianuu.essentials.permission.PermissionManager
import com.ivianuu.essentials.unlock.UnlockScreenUseCase
import com.ivianuu.essentials.util.AppCoroutineDispatchers
import com.ivianuu.essentials.util.Logger
import com.ivianuu.injekt.Transient
import kotlinx.coroutines.withContext

@Transient
class ActionExecutors(
    private val actionStore: ActionStore,
    private val dispatchers: AppCoroutineDispatchers,
    private val logger: Logger,
    private val permissionManager: PermissionManager,
    private val unlockScreenUseCase: UnlockScreenUseCase
) {

    suspend fun execute(key: String) = withContext(dispatchers.computation) {
        logger.d("execute $key")
        val action = actionStore.getAction(key)

        // check permissions
        if (!permissionManager.request(action.permissions)) {
            logger.d("couldn't get permissions for $key")
            return@withContext
        }

        // unlock screen
        if (action.unlockScreen && !unlockScreenUseCase()) {
            logger.d("couldn't unlock screen for $key")
            return@withContext
        }

        logger.d("fire $key")

        // fire
        try {
            action.executor()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
