/*
 * Copyright 2019 Manuel Wrage
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
import android.content.pm.ApplicationInfo
import com.ivianuu.essentials.util.ComponentBuilderInterceptor
import com.ivianuu.essentials.util.containsFlag
import com.ivianuu.injekt.Component
import com.ivianuu.injekt.ComponentOwner
import com.ivianuu.injekt.Injekt
import com.ivianuu.injekt.android.AndroidLogger
import com.ivianuu.injekt.android.ApplicationComponent

/**
 * App
 */
abstract class EsApp : Application(), ComponentOwner, ComponentBuilderInterceptor {

    override val component: Component
        get() {
            if (!AppComponentHolder.isInitialized) {
                configureInjekt()
                initializeComponent()
            }

            return AppComponentHolder.component
        }

    protected open fun configureInjekt() {
        if (applicationInfo.flags.containsFlag(ApplicationInfo.FLAG_DEBUGGABLE)) {
            Injekt {
                logger = AndroidLogger()
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        component
    }

    protected open fun initializeComponent() {
        AppComponentHolder.init(
            ApplicationComponent(this) {
                buildComponent()
            }
        )
    }
}
