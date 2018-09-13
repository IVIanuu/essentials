
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

package com.ivianuu.essentials.sample.ui

import android.os.Bundle
import com.ivianuu.essentials.ui.base.BaseActivity
import com.ivianuu.essentials.ui.base.BaseActivityModule
import com.ivianuu.essentials.ui.mvrx.MvRxState
import com.ivianuu.essentials.ui.mvrx.MvRxViewModel
import com.ivianuu.essentials.util.ext.bindViewModel
import com.ivianuu.essentials.util.ext.d
import com.ivianuu.essentials.util.lifecycle.LiveEvent
import com.ivianuu.essentials.util.lifecycle.mutableLiveEvent
import dagger.Module
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainActivity : BaseActivity() {

    override val startDestination: Any?
        get() = CounterDestination(1)

    private val viewModel by bindViewModel<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.myEvent.consume { d { "on event" } }
    }
}

class MainViewModel @Inject constructor() : MvRxViewModel<MainState>(
    MainState
) {

    val myEvent: LiveEvent<Unit>
        get() = _myEvent
    private val _myEvent = mutableLiveEvent<Unit>()

    init {
        d { "offer event" }
        _myEvent.offer(Unit)

        launch {
            delay(3000)
            d { "offer event" }
            _myEvent.offer(Unit)
            delay(3000)
            d { "offer event" }
            _myEvent.offer(Unit)
        }
    }
}

object MainState : MvRxState

@Module
abstract class MainActivityModule : BaseActivityModule<MainActivity>()