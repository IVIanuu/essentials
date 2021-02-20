/*
 * Copyright 2020 Manuel Wrage
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

package com.ivianuu.essentials.app

import android.app.Application
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.component.AppComponent
import com.ivianuu.injekt.component.ComponentElementBinding
import com.ivianuu.injekt.component.appComponent
import com.ivianuu.injekt.component.get

/**
 * App
 */
abstract class EsApp : Application() {
    override fun onCreate() {
        with(appComponent.get<EsAppComponent>()) {
            appInitializerRunner()
            appWorkerRunner()
        }
        super.onCreate()
    }
}

@ComponentElementBinding<AppComponent>
@Given class EsAppComponent(
    @Given val appInitializerRunner: AppInitializerRunner,
    @Given val appWorkerRunner: AppWorkerRunner
)
