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

package com.ivianuu.essentials.ui.state

import android.view.View
import com.ivianuu.essentials.ui.base.BaseViewModelController
import com.ivianuu.essentials.util.ext.subscribeForUi

/**
 * Controller with a single state
 */
abstract class StateController<T : Any, VM : StateViewModel<T>> : BaseViewModelController<VM>() {

    override fun onAttach(view: View) {
        super.onAttach(view)

        viewModel.state.subscribeForUi(this, this::render)
    }

    protected abstract fun render(state: T)
}