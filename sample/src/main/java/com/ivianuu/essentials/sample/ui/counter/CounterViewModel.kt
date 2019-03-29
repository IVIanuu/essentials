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

import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.ivianuu.essentials.injection.PerController
import com.ivianuu.essentials.sample.ui.checkapps.CheckAppsKey
import com.ivianuu.essentials.sample.ui.list.ListKey
import com.ivianuu.essentials.sample.work.MyWorkerOne
import com.ivianuu.essentials.sample.work.MyWorkerTwo
import com.ivianuu.essentials.ui.mvrx.MvRxViewModel
import com.ivianuu.essentials.ui.traveler.NavOptions
import com.ivianuu.essentials.ui.traveler.horizontal
import com.ivianuu.injekt.annotations.Factory
import com.ivianuu.traveler.Router
import com.ivianuu.traveler.goBack
import com.ivianuu.traveler.navigate
import com.ivianuu.traveler.popToRoot

@Factory(PerController::class)
class CounterViewModel(
    key: CounterKey,
    private val router: Router
) : MvRxViewModel<CounterState>(CounterState(key.screen)) {

    fun screenUpClicked() {
        withState {
            router.navigate(
                CounterKey(it.screen.inc()),
                NavOptions().horizontal()
            )
        }
    }

    fun screenDownClicked() {
        router.goBack()
    }

    fun rootScreenClicked() {
        router.popToRoot()
    }

    fun listScreenClicked() {
        router.navigate(ListKey())
    }

    fun checkAppsClicked() {
        router.navigate(CheckAppsKey())
    }

    fun doWorkClicked() {
        WorkManager.getInstance().enqueue(
            OneTimeWorkRequestBuilder<MyWorkerOne>().build()
        )

        WorkManager.getInstance().enqueue(
            OneTimeWorkRequestBuilder<MyWorkerTwo>().build()
        )
    }
}

data class CounterState(val screen: Int)