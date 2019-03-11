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

package com.ivianuu.essentials.ui.viewmodel.director

import android.os.Bundle
import com.ivianuu.director.Controller
import com.ivianuu.director.ControllerListener
import com.ivianuu.director.activity
import com.ivianuu.director.doOnPostDestroy
import com.ivianuu.director.retained.retained
import com.ivianuu.essentials.ui.viewmodel.ViewModelManager
import com.ivianuu.essentials.ui.viewmodel.ViewModelManagerOwner
import com.ivianuu.essentials.util.getSavedState
import com.ivianuu.essentials.util.putSavedState

/**
 * @author Manuel Wrage (IVIanuu)
 */
class ControllerViewModelManagerOwner(
    private val controller: Controller
) : ControllerListener, ViewModelManagerOwner {

    override val viewModelManager by controller.retained(initializer = ::ViewModelManager)

    init {
        controller.addListener(this)
    }

    override fun onRestoreInstanceState(controller: Controller, savedInstanceState: Bundle) {
        viewModelManager.restoreState(savedInstanceState.getSavedState(KEY_VIEW_MODELS))
    }

    override fun onSaveInstanceState(controller: Controller, outState: Bundle) {
        outState.putSavedState(KEY_VIEW_MODELS, viewModelManager.saveState())
    }

    override fun postDestroy(controller: Controller) {
        super.postDestroy(controller)
        if (!controller.activity.isChangingConfigurations) {
            viewModelManager.destroy()
        }
    }

    private companion object {
        private const val KEY_VIEW_MODELS = "ControllerViewModelManagerOwner.viewModels"
    }
}

private val viewModelManagerOwnersByController =
    mutableMapOf<Controller, ViewModelManagerOwner>()

val Controller.viewModelManagerOwner: ViewModelManagerOwner
    get() = viewModelManagerOwnersByController.getOrPut(this) {
        ControllerViewModelManagerOwner(this).also {
            doOnPostDestroy { viewModelManagerOwnersByController.remove(this) }
        }
    }

val Controller.viewModelManager: ViewModelManager
    get() = viewModelManagerOwner.viewModelManager