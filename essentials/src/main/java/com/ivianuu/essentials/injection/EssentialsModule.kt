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

package com.ivianuu.essentials.injection

import com.ivianuu.essentials.injection.director.DirectorInjectionModule
import com.ivianuu.essentials.injection.view.ViewInjectionModule
import com.ivianuu.essentials.injection.viewmodel.ViewModelInjectionModule
import com.ivianuu.essentials.injection.worker.WorkerInjectionModule
import com.ivianuu.essentials.ui.traveler.TravelerModule
import dagger.Module
import dagger.android.AndroidInjectionModule
import dagger.android.support.AndroidSupportInjectionModule

/**
 * Wraps all essential modules
 */
@Module(
    includes = [
        AndroidInjectionModule::class,
        AndroidSupportInjectionModule::class,
        DirectorInjectionModule::class,
        EssentialsAppServiceModule::class,
        SystemServiceModule::class,
        TravelerModule::class,
        ViewInjectionModule::class,
        ViewModelInjectionModule::class,
        WorkerInjectionModule::class
    ]
)
object EssentialsModule