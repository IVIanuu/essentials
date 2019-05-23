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
import com.ivianuu.essentials.sample.ui.checkapps.CheckAppsKey
import com.ivianuu.essentials.sample.ui.list.ListKey
import com.ivianuu.essentials.sample.work.MyWorkerOne
import com.ivianuu.essentials.sample.work.MyWorkerTwo
import com.ivianuu.essentials.ui.mvrx.MvRxViewModel
import com.ivianuu.traveler.*

class CounterViewModel(
    key: CounterKey,
    private val router: Router
) : MvRxViewModel<CounterState>(CounterState(key.screen)) {

    fun screenUpClicked() {
        withState {
            router.navigate(CounterKey(it.screen.inc()))
        }
    }

    fun screenDownClicked() {
        withState {
            if (it.screen == 1) {
                router.finish()
            } else {
                router.goBack()
            }
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
        with(WorkManager.getInstance()) {
            enqueue(OneTimeWorkRequestBuilder<MyWorkerOne>().build())
            enqueue(OneTimeWorkRequestBuilder<MyWorkerTwo>().build())
        }
    }
}

data class CounterState(val screen: Int)