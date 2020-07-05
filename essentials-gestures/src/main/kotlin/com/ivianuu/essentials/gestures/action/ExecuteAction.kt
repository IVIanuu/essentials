package com.ivianuu.essentials.gestures.action

import com.ivianuu.essentials.permission.PermissionManager
import com.ivianuu.essentials.unlock.UnlockScreen
import com.ivianuu.essentials.unlock.unlockScreen
import com.ivianuu.essentials.util.AppCoroutineDispatchers
import com.ivianuu.essentials.util.Logger
import com.ivianuu.essentials.util.d
import com.ivianuu.essentials.util.dispatchers
import com.ivianuu.injekt.Reader
import com.ivianuu.injekt.Unscoped
import com.ivianuu.injekt.get
import kotlinx.coroutines.withContext

@Reader
suspend fun executeAction(key: String) = withContext(dispatchers.default) {
    d("execute $key")
    val action = getAction(key)

    // check permissions
    if (!get<PermissionManager>().request(action.permissions)) {
        d("couldn't get permissions for $key")
        return@withContext
    }

    // unlock screen
    if (action.unlockScreen && !unlockScreen()) {
        d("couldn't unlock screen for $key")
        return@withContext
    }

    d("fire $key")

    // fire
    try {
        action.executor()
    } catch (e: Exception) {
        e.printStackTrace()
    }
}
