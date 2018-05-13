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

package com.ivianuu.essentials.sample.app

import android.app.Application
import com.ivianuu.contributer.annotations.AndroidInjectorKeyRegistry
import com.ivianuu.essentials.injection.EssentialsModule
import com.ivianuu.essentials.injection.conductor.ControllerKey
import com.ivianuu.essentials.sample.injection.ActivityBindingModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import javax.inject.Singleton

/**
 * App component
 */
@AndroidInjectorKeyRegistry(keys = [ControllerKey::class])
@Singleton
@Component(
    modules = [
        ActivityBindingModule::class,
        EssentialsModule::class
    ]
)
interface AppComponent : AndroidInjector<App> {

    @Component.Builder
    interface Builder {
        @BindsInstance fun app(app: Application): Builder
        fun build(): AppComponent
    }

}