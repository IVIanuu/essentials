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
import com.ivianuu.androidktx.appcompat.widget.setTextFuture
import com.ivianuu.compass.Destination
import com.ivianuu.compass.Detour
import com.ivianuu.compass.fragment.destination
import com.ivianuu.essentials.sample.R
import com.ivianuu.essentials.ui.base.BaseFragment
import com.ivianuu.essentials.ui.mvrx.bindViewModel
import com.ivianuu.essentials.ui.mvrx.withState
import com.ivianuu.essentials.ui.traveler.detour.HorizontalDetour
import kotlinx.android.synthetic.main.fragment_counter.*

@Detour(HorizontalDetour::class)
@Destination(CounterFragment::class)
data class CounterDestination(val screen: Int)

/**
 * @author Manuel Wrage (IVIanuu)
 */
class CounterFragment : BaseFragment() {

    override val layoutRes = R.layout.fragment_counter

    private val viewModel by bindViewModel(CounterViewModel::class)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.setDestination(destination())
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
        do_work.setOnClickListener { viewModel.doWorkClicked() }
    }

    override fun invalidate() {
        withState(viewModel) { count.setTextFuture("Screen: ${it.screen}, Count: ${it.count}") }
    }

}