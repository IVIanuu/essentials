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

import android.os.Bundle
import android.view.View
import com.ivianuu.essentials.ui.base.BaseViewModelFragment
import com.ivianuu.essentials.util.ext.subscribeForUi

/**
 * Fragment with a single state
 */
abstract class StateFragment<T : Any, VM : StateViewModel<T>> : BaseViewModelFragment<VM>() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.state.subscribeForUi(viewLifecycleScopeProvider, this::render)
    }

    protected abstract fun render(state: T)
}