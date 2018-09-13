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

package com.ivianuu.essentials.sample.ui.counter

import android.os.Bundle
import android.view.View
import com.ivianuu.compass.Destination
import com.ivianuu.compass.Detour
import com.ivianuu.essentials.sample.R
import com.ivianuu.essentials.ui.base.BaseFragment
import com.ivianuu.essentials.ui.mvrx.bindViewModel
import com.ivianuu.essentials.ui.mvrx.withState
import com.ivianuu.essentials.ui.traveler.detour.HorizontalDetour
import com.ivianuu.essentials.util.ext.applyIf
import com.ivianuu.essentials.util.ext.bindDelegate
import com.ivianuu.essentials.util.ext.d
import com.ivianuu.essentials.util.ext.setTextFuture
import kotlinx.android.synthetic.main.fragment_counter.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine

interface CounterFragmentDelegate

class CounterFragmentDelegateImpl :
    CounterFragmentDelegate {
    fun init(counterFragment: CounterFragment) {
        d { "init called" }
    }
}

@Detour(HorizontalDetour::class)
@Destination(CounterFragment::class)
data class CounterDestination(val screen: Int)

/**
 * @author Manuel Wrage (IVIanuu)
 */
class CounterFragment : BaseFragment(), CounterFragmentDelegate by CounterFragmentDelegateImpl() {

    override val layoutRes = R.layout.fragment_counter

    private val delegate by bindDelegate(CounterFragmentDelegateImpl::class)
    private val viewModel by bindViewModel(CounterViewModel::class)

    init {
        applyIf(true) { }
        delegate.init(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.setDestination(counterDestination())

        launch {
            suspendCancellableCoroutine<Unit> {
                it.invokeOnCancellation { d { "on cancel" } }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        increase.setOnClickListener { viewModel.increaseClicked() }
        decrease.setOnClickListener { viewModel.decreaseClicked() }
        reset.setOnClickListener { viewModel.resetClicked() }

        screen_up.setOnClickListener { viewModel.screenUpClicked() }
        screen_down.setOnClickListener { viewModel.screenDownClicked() }
        root_screen.setOnClickListener { viewModel.rootScreenClicked() }
        list_screen.setOnClickListener { viewModel.listScreenClicked() }

        viewCoroutineScope.launch {
            suspendCancellableCoroutine<Unit> {
                it.invokeOnCancellation { d { "view on cancel" } }
            }
        }
    }

    override fun invalidate() {
        withState(viewModel) { count.setTextFuture("Screen: ${it.screen}, Count: ${it.count}") }
    }
}