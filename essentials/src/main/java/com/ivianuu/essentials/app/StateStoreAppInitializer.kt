package com.ivianuu.essentials.app

import android.app.Application
import com.ivianuu.statestore.StateStorePlugins
import com.ivianuu.statestore.android.MAIN_THREAD_EXECUTOR

/**
 * Sets the default callback executor to the main thread
 */
class StateStoreAppInitializer : AppInitializer {

    override fun initialize(app: Application) {
        StateStorePlugins.defaultCallbackExecutor = MAIN_THREAD_EXECUTOR
    }

}