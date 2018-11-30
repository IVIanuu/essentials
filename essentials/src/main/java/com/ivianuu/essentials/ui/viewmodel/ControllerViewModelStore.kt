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

package com.ivianuu.essentials.ui.viewmodel

import android.os.Bundle
import com.ivianuu.director.Controller
import com.ivianuu.director.ControllerLifecycleListener

/**
 * Controller view model helper
 */
class ControllerViewModelStore(controller: Controller, savedInstanceState: Bundle?) :
    ViewModelStore() {

    init {
        restoreInstanceState(savedInstanceState)
    }

    private val lifecycleListener = object : ControllerLifecycleListener {

        override fun onSaveInstanceState(controller: Controller, outState: Bundle) {
            super.onSaveInstanceState(controller, outState)
            outState.putBundle(KEY_VIEW_MODEL_STORE, saveInstanceState())
        }

        override fun postDestroy(controller: Controller) {
            super.postDestroy(controller)
            if (!controller.activity.isChangingConfigurations) {
                clear()
            }
        }
    }

    init {
        controller.addLifecycleListener(lifecycleListener)
    }

    private companion object {
        private const val KEY_VIEW_MODEL_STORE = "ControllerViewModelStore.viewModelStore"
    }
}