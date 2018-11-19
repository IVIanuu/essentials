/*
 * Copyright 2018 Manuel Wrage
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ivianuu.essentials.app

import android.app.Application
import com.ivianuu.statestore.StateStorePlugins
import com.ivianuu.statestore.android.MAIN_THREAD_EXECUTOR
import javax.inject.Inject

/**
 * Sets the default callback executor to the main thread
 */
class StateStoreAppInitializer @Inject constructor() : AppInitializer {

    override fun initialize(app: Application) {
        StateStorePlugins.defaultCallbackExecutor = MAIN_THREAD_EXECUTOR
    }

}