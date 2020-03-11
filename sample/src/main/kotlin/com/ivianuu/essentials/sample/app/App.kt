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

package com.ivianuu.essentials.sample.app

import com.ivianuu.essentials.android.app.EsApp
import com.ivianuu.essentials.apps.coil.EsAppsCoilModule
import com.ivianuu.essentials.billing.EsBillingModule
import com.ivianuu.essentials.boot.EsBootModule
import com.ivianuu.essentials.coil.EsCoilModule
import com.ivianuu.essentials.gestures.EsGesturesModule
import com.ivianuu.essentials.permission.EsPermissionModule
import com.ivianuu.essentials.sample.BootModule
import com.ivianuu.essentials.sample.ui.UiModule
import com.ivianuu.essentials.sample.work.WorkModule
import com.ivianuu.essentials.twilight.EsTwilightModule
import com.ivianuu.essentials.work.WorkInjectionModule
import com.ivianuu.injekt.ComponentBuilder

class App : EsApp() {

    override fun ComponentBuilder.buildComponent() {
        modules(
            EsAppsCoilModule,
            EsBillingModule,
            EsBootModule,
            EsCoilModule,
            EsGesturesModule,
            EsPermissionModule,
            EsTwilightModule,
            WorkInjectionModule,

            AppModule,
            BootModule,
            UiModule,
            WorkModule
        )
    }

}