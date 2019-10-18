/*
 * Copyright 2019 Manuel Wrage
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
import com.ivianuu.essentials.ui.base.EsController
import com.ivianuu.essentials.ui.mvrx.injekt.injectMvRxViewModel
import com.ivianuu.essentials.ui.navigation.director.controllerRoute
import com.ivianuu.essentials.ui.navigation.director.controllerRouteOptions
import com.ivianuu.essentials.ui.navigation.director.horizontal
import com.ivianuu.injekt.Inject
import com.ivianuu.injekt.Param
import com.ivianuu.injekt.parametersOf
import kotlinx.android.synthetic.main.controller_counter.*

fun counterRoute(screen: Int) = controllerRoute<CounterController>(
    options = controllerRouteOptions().horizontal()
) { parametersOf(screen) }

@Inject
class CounterController(@Param private val screen: Int) : EsController() {

    override val layoutRes: Int get() = R.layout.controller_counter

    private val viewModel: CounterViewModel by injectMvRxViewModel {
        parametersOf(screen)
    }

    override fun onViewCreated(view: View) {
        super.onViewCreated(view)

        screen_up.setOnClickListener { viewModel.screenUpClicked() }
        screen_down.setOnClickListener { viewModel.screenDownClicked() }
        root_screen.setOnClickListener { viewModel.rootScreenClicked() }
        list_screen.setOnClickListener { viewModel.listScreenClicked() }
        check_apps.setOnClickListener { viewModel.checkAppsClicked() }
        do_work.setOnClickListener { viewModel.doWorkClicked() }
        nav_bar.setOnClickListener { viewModel.navBarClicked() }
        twilight.setOnClickListener { viewModel.twilightClicked() }
    }

    override fun invalidate() {
        count.text = "Screen: ${viewModel.state.screen}"
    }

}