package com.ivianuu.essentials.gestures.action

import com.ivianuu.essentials.permission.requestPermissions
import com.ivianuu.essentials.unlock.unlockScreen
import com.ivianuu.essentials.util.DefaultDispatcher
import com.ivianuu.essentials.util.Logger
import com.ivianuu.essentials.util.showToast
import com.ivianuu.injekt.Assisted
import com.ivianuu.injekt.FunBinding
import kotlinx.coroutines.withContext

@FunBinding
suspend fun executeAction(
    defaultDispatcher: DefaultDispatcher,
    logger: Logger,
    getAction: getAction,
    requestPermissions: requestPermissions,
    unlockScreen: unlockScreen,
    showToast: showToast,
    key: @Assisted String,
): Unit = withContext(defaultDispatcher) {
    logger.d("execute $key")
    val action = getAction(key)

    // check permissions
    if (!requestPermissions(action.permissions)) {
        logger.d("couldn't get permissions for $key")
        return@withContext
    }

    // unlock screen
    if (action.unlockScreen && !unlockScreen()) {
        logger.d("couldn't unlock screen for $key")
        return@withContext
    }

    logger.d("fire $key")

    // fire
    try {
        action.execute()
    } catch (t: Throwable) {
        t.printStackTrace()
        showToast("Failed to execute '${action.title}'") // todo res
    }
}
