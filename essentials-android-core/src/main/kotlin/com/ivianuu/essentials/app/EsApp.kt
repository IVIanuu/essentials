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
import com.ivianuu.essentials.componentElementBinding
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.GivenGroup
import com.ivianuu.injekt.android.applicationComponent
import com.ivianuu.injekt.component.ApplicationScoped
import com.ivianuu.injekt.component.Component
import com.ivianuu.injekt.component.get

/**
 * App
 */
abstract class EsApp : Application() {
    override fun onCreate() {
        with(applicationComponent[EsAppDependencies]) {
            runInitializers()
            runAppWorkers()
        }
        super.onCreate()
    }
}

@Given class EsAppDependencies(
    @Given val runInitializers: runInitializers,
    @Given val runAppWorkers: runAppWorkers
) {
    companion object : Component.Key<EsAppDependencies> {
        @GivenGroup val binding = componentElementBinding(ApplicationScoped, this)
    }
}
