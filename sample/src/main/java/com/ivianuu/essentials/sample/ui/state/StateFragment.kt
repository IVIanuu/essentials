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

package com.ivianuu.essentials.sample.ui.state

import android.os.Bundle
import android.view.View
import com.ivianuu.androidktx.appcompat.widget.textFuture
import com.ivianuu.androidktx.lifecycle.bindViewModel
import com.ivianuu.compass.Destination
import com.ivianuu.essentials.sample.R
import com.ivianuu.essentials.ui.base.BaseFragment
import com.ivianuu.essentials.ui.common.BaseViewModel
import kotlinx.android.synthetic.main.fragment_state.*
import javax.inject.Inject

@Destination(StateFragment::class)
object StateDestination

/**
 * @author Manuel Wrage (IVIanuu)
 */
class StateFragment : BaseFragment() {

    override val layoutRes = R.layout.fragment_state

    private val viewModel by bindViewModel<StateViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        increment.setOnClickListener { viewModel.incrementClicked() }
        decrement.setOnClickListener { viewModel.decrementClicked() }

        viewModel.state.subscribeUi { count.textFuture = "Count: ${it.count}" }
    }
}

class StateViewModel @Inject constructor() : BaseViewModel() {

    val state get() = stateStore.observable

    private val stateStore = RealStateStore(State())

    override fun onCleared() {
        super.onCleared()
        stateStore.close()
    }

    fun incrementClicked() {
        stateStore.setState { copy(count = count.inc()) }
    }

    fun decrementClicked() {
        stateStore.setState { copy(count = count.dec()) }
    }
}

data class State(val count: Int = 0)