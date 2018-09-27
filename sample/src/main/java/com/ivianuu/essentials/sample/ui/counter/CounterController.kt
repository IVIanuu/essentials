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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ivianuu.androidktx.appcompat.widget.textFuture
import com.ivianuu.compass.Destination
import com.ivianuu.compass.Detour
import com.ivianuu.compass.director.ControllerDetour
import com.ivianuu.director.RouterTransaction
import com.ivianuu.director.common.HorizontalChangeHandler
import com.ivianuu.director.common.contextRef
import com.ivianuu.director.popChangeHandler
import com.ivianuu.director.pushChangeHandler
import com.ivianuu.essentials.sample.R
import com.ivianuu.essentials.ui.base.BaseController
import com.ivianuu.essentials.ui.mvrx.bindViewModel
import com.ivianuu.essentials.ui.mvrx.withState
import dagger.Subcomponent
import dagger.android.AndroidInjector
import kotlinx.android.synthetic.main.fragment_counter.*
import javax.inject.Inject

class CounterDetour : ControllerDetour<CounterDestination> {
    override fun setupTransaction(
        destination: CounterDestination,
        data: Any?,
        transaction: RouterTransaction
    ) {
        transaction
            .pushChangeHandler(HorizontalChangeHandler())
            .popChangeHandler(HorizontalChangeHandler())
    }
}

@Detour(CounterDetour::class)
@Destination(CounterController::class)
data class CounterDestination(val screen: Int)

/**
 * @author Manuel Wrage (IVIanuu)
 */
class CounterController : BaseController() {

    @set:Inject var counterViewModelFactory: CounterViewModelFactory by contextRef()

    override val layoutRes = R.layout.fragment_counter

    private val viewModel by bindViewModel(CounterViewModel::class) {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return counterViewModelFactory.create(counterDestination()) as T
            }
        }
    }

    override fun onViewCreated(view: View) {
        super.onViewCreated(view)

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
        withState(viewModel) { count.textFuture = "Screen: ${it.screen}, Count: ${it.count}" }
    }
}

@Subcomponent
interface CounterControllerSubcomponent : AndroidInjector<CounterController> {
    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<CounterController>()
}