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

import com.ivianuu.essentials.hidenavbar.NavBarConfig
import com.ivianuu.essentials.hidenavbar.NavBarController
import com.ivianuu.essentials.sample.ui.checkapps.checkAppsRoute
import com.ivianuu.essentials.sample.ui.list.listRoute
import com.ivianuu.essentials.sample.ui.material.materialListRoute
import com.ivianuu.essentials.sample.ui.widget2.WidgetController2
import com.ivianuu.essentials.sample.util.SecureSettingsHelper
import com.ivianuu.essentials.sample.work.WorkScheduler
import com.ivianuu.essentials.securesettings.secureSettingsRoute
import com.ivianuu.essentials.twilight.twilightSettingsRoute
import com.ivianuu.essentials.ui.mvrx.MvRxViewModel
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.ui.navigation.director.controllerRoute
import com.ivianuu.essentials.ui.navigation.director.controllerRouteOptions
import com.ivianuu.essentials.ui.navigation.director.copy
import com.ivianuu.essentials.ui.navigation.director.horizontal
import com.ivianuu.injekt.Inject
import com.ivianuu.injekt.Param

@Inject
class CounterViewModel(
    @Param screen: Int,
    private val navigator: Navigator,
    private val navBarController: NavBarController,
    private val secureSettingsHelper: SecureSettingsHelper,
    private val workScheduler: WorkScheduler
) : MvRxViewModel<CounterState>(CounterState(screen)) {

    private var navBarHidden = false

    fun screenUpClicked() {
        navigator.push(counterRoute(state.screen.inc()))
    }

    fun screenDownClicked() {
        navigator.pop()
    }

    fun rootScreenClicked() {
        // todo router.popToRoot()
    }

    fun listScreenClicked() {
        navigator.push(listRoute)
    }

    fun checkAppsClicked() {
        navigator.push(checkAppsRoute)
    }

    fun doWorkClicked() {
        workScheduler.scheduleWork()
    }

    fun twilightClicked() {
        navigator.push(
            twilightSettingsRoute.copy(
                options = controllerRouteOptions().horizontal()
            )
        )
    }

    fun materialListClicked() {
        navigator.push(materialListRoute)
    }

    fun widgetsClicked() {
        navigator.push(controllerRoute<WidgetController2>())
    }

    fun navBarClicked() {
        if (secureSettingsHelper.canWriteSecureSettings()) {
            navBarHidden = !navBarHidden
            navBarController.setNavBarConfig(NavBarConfig(navBarHidden))
        } else {
            navigator.push(secureSettingsRoute(true))
        }
    }
}

data class CounterState(val screen: Int)