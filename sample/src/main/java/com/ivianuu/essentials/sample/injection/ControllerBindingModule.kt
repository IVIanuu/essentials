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

package com.ivianuu.essentials.sample.injection

import com.ivianuu.contributor.ContributeInjector
import com.ivianuu.essentials.injection.PerController
import com.ivianuu.essentials.sample.ui.counter.CounterController
import com.ivianuu.essentials.sample.ui.list.ListController
import dagger.Module

@Module
abstract class ControllerBindingModule {

    @PerController
    @ContributeInjector
    abstract fun bindCounterController(): CounterController

    @PerController
    @ContributeInjector
    abstract fun bindListController(): ListController

}