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

import android.view.View
import com.ivianuu.essentials.sample.R
import com.ivianuu.essentials.ui.base.BaseController
import com.ivianuu.essentials.ui.mvrx.bindViewModel
import com.ivianuu.essentials.ui.mvrx.withState
import com.ivianuu.essentials.ui.traveler.anim.HorizontalControllerKeySetup
import com.ivianuu.essentials.ui.traveler.key.ControllerKey
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.controller_counter.*

@Parcelize
data class CounterKey(val screen: Int) : ControllerKey(
    CounterController::class,
    HorizontalControllerKeySetup()
)

/**
 * @author Manuel Wrage (IVIanuu)
 */
class CounterController : BaseController() {

    override val layoutRes get() = R.layout.controller_counter

    private val viewModel by bindViewModel(CounterViewModel::class)

    override fun onCreate() {
        super.onCreate()
        viewModel.setKey(key())
    }

    override fun onBindView(view: View) {
        super.onBindView(view)

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
        withState(viewModel) { count.text = "Screen: ${it.screen}, Count: ${it.count}" }
    }

}