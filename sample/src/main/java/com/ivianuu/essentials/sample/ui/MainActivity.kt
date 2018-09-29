
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

import com.ivianuu.essentials.sample.ui.counter.CounterDestination
import com.ivianuu.essentials.ui.base.BaseActivity
import com.ivianuu.essentials.ui.base.BaseActivityModule
import dagger.Module

class MainActivity : BaseActivity() {

    override val startDestination: Any?
        get() = CounterDestination(1)

    override fun navigators() =
        listOf(MaterialDialogNavigatorPlugin(this))

    override val useDirector: Boolean
        get() = true

}

@Module
abstract class MainActivityModule : BaseActivityModule<MainActivity>()