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

import com.ivianuu.essentials.hidenavbar.NavBarConfig
import com.ivianuu.essentials.hidenavbar.NavBarController
import com.ivianuu.essentials.sample.ui.checkapps.CheckAppsKey
import com.ivianuu.essentials.sample.ui.list.ListKey
import com.ivianuu.essentials.sample.util.SecureSettingsHelper
import com.ivianuu.essentials.sample.work.WorkScheduler
import com.ivianuu.essentials.securesettings.SecureSettingsKey
import com.ivianuu.essentials.ui.mvrx.MvRxViewModel
import com.ivianuu.essentials.ui.traveler.NavOptions
import com.ivianuu.essentials.ui.traveler.horizontal
import com.ivianuu.essentials.ui.twilight.TwilightSettingsKey
import com.ivianuu.injekt.Inject
import com.ivianuu.traveler.Router
import com.ivianuu.traveler.finish
import com.ivianuu.traveler.goBack
import com.ivianuu.traveler.navigate
import com.ivianuu.traveler.popToRoot

@Inject
class CounterViewModel(
    key: CounterKey,
    private val router: Router,
    private val navBarController: NavBarController,
    private val secureSettingsHelper: SecureSettingsHelper,
    private val workScheduler: WorkScheduler
) : MvRxViewModel<CounterState>(CounterState(key.screen)) {

    private var navBarHidden = false

    fun screenUpClicked() {
        router.navigate(CounterKey(state.screen.inc()))
    }

    fun screenDownClicked() {
        if (state.screen == 1) {
            router.finish()
        } else {
            router.goBack()
        }
    }

    fun rootScreenClicked() {
        router.popToRoot()
    }

    fun listScreenClicked() {
        router.navigate(ListKey)
    }

    fun checkAppsClicked() {
        router.navigate(CheckAppsKey)
    }

    fun doWorkClicked() {
        workScheduler.scheduleWork()
    }

    fun twilightClicked() {
        router.navigate(
            TwilightSettingsKey,
            NavOptions().horizontal()
        )
    }

    fun navBarClicked() {
        if (secureSettingsHelper.canWriteSecureSettings()) {
            navBarHidden = !navBarHidden
            navBarController.setNavBarConfig(NavBarConfig(navBarHidden))
        } else {
            router.navigate(SecureSettingsKey(true))
        }
    }
}

data class CounterState(val screen: Int)