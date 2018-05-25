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
import com.ivianuu.daggerextensions.AutoComponent
import com.ivianuu.daggerextensions.BindsTypes
import com.ivianuu.essentials.app.BaseApp
import com.ivianuu.essentials.injection.ActivityBindingModule_
import com.ivianuu.essentials.injection.EssentialsModule
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication
import javax.inject.Singleton

/**
 * @author Manuel Wrage (IVIanuu)
 */
@BindsTypes(types = [Application::class])
@Singleton
@AutoComponent(
    modules = [
        ActivityBindingModule_::class,
        EssentialsModule::class
    ]
)
class App : BaseApp() {

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerAppComponent.builder()
            .app(this)
            .build()
    }

}