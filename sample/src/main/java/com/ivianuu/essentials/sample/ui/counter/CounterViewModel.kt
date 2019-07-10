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
import com.ivianuu.essentials.sample.ui.checkapps.CheckAppsKey
import com.ivianuu.essentials.sample.ui.list.ListKey
import com.ivianuu.essentials.sample.util.SecureSettingsHelper
import com.ivianuu.essentials.sample.work.WorkScheduler
import com.ivianuu.essentials.securesettings.SecureSettingsKey
import com.ivianuu.essentials.ui.mvrx.MvRxViewModel
import com.ivianuu.essentials.ui.traveler.NavOptions
import com.ivianuu.essentials.ui.traveler.horizontal
import com.ivianuu.essentials.twilight.TwilightSettingsKey
import com.ivianuu.injekt.Inject
import com.ivianuu.traveler.Router
import com.ivianuu.traveler.finish
import com.ivianuu.traveler.pop
import com.ivianuu.traveler.popToRoot
import com.ivianuu.traveler.push

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
        router.push(CounterKey(state.screen.inc()))
    }

    fun screenDownClicked() {
        if (state.screen == 1) {
            router.finish()
        } else {
            router.pop()
        }
    }

    fun rootScreenClicked() {
        router.popToRoot()
    }

    fun listScreenClicked() {
        router.push(ListKey)
    }

    fun checkAppsClicked() {
        router.push(CheckAppsKey)
    }

    fun doWorkClicked() {
        workScheduler.scheduleWork()
    }

    fun twilightClicked() {
        router.push(
            TwilightSettingsKey,
            NavOptions().horizontal()
        )
    }

    fun navBarClicked() {
        if (secureSettingsHelper.canWriteSecureSettings()) {
            navBarHidden = !navBarHidden
            navBarController.setNavBarConfig(NavBarConfig(navBarHidden))
        } else {
            router.push(SecureSettingsKey(true))
        }
    }
}

data class CounterState(val screen: Int)