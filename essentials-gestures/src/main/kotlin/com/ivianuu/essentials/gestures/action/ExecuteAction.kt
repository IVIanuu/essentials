package com.ivianuu.essentials.gestures.action

import com.ivianuu.essentials.permission.requestPermissions
import com.ivianuu.essentials.unlock.unlockScreen
import com.ivianuu.essentials.util.Toaster
import com.ivianuu.essentials.util.d
import com.ivianuu.essentials.util.dispatchers
import com.ivianuu.injekt.Reader
import kotlinx.coroutines.withContext

@Reader
suspend fun executeAction(key: String) = withContext(dispatchers.default) {
    d { "execute $key" }
    val action = getAction(key)

    // check permissions
    if (!requestPermissions(action.permissions)) {
        d { "couldn't get permissions for $key" }
        return@withContext
    }

    // unlock screen
    if (action.unlockScreen && !unlockScreen()) {
        d { "couldn't unlock screen for $key" }
        return@withContext
    }

    d { "fire $key" }

    // fire
    try {
        action.execute()
    } catch (e: Exception) {
        e.printStackTrace()
        Toaster.toast("Failed to execute '${action.title}'") // todo res
    }
}
