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

import com.ivianuu.director.Controller
import com.ivianuu.essentials.injection.director.ControllerKey
import com.ivianuu.essentials.sample.ui.counter.CounterController
import com.ivianuu.essentials.sample.ui.counter.CounterControllerSubcomponent
import com.ivianuu.essentials.sample.ui.list.ListController
import com.ivianuu.essentials.sample.ui.list.ListControllerSubcomponent
import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.multibindings.IntoMap

@Module(
    subcomponents = [
        CounterControllerSubcomponent::class,
        ListControllerSubcomponent::class
    ]
)
abstract class ControllerBindingModule {

    @Binds
    @IntoMap
    @ControllerKey(CounterController::class)
    abstract fun bindCounterControllerInjectorFactory(
        builder: CounterControllerSubcomponent.Builder
    ): AndroidInjector.Factory<out Controller>

    @Binds
    @IntoMap
    @ControllerKey(ListController::class)
    abstract fun bindListControllerInjectorFactory(
        builder: ListControllerSubcomponent.Builder
    ): AndroidInjector.Factory<out Controller>
}