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

import com.ivianuu.essentials.app.EsApp
import com.ivianuu.essentials.apps.coil.esAppsCoilBindings
import com.ivianuu.essentials.billing.esBilling
import com.ivianuu.essentials.boot.esBootInjection
import com.ivianuu.essentials.coil.esCoil
import com.ivianuu.essentials.gestures.esGestures
import com.ivianuu.essentials.permission.esPermissions
import com.ivianuu.essentials.sample.bootBindings
import com.ivianuu.essentials.sample.ui.uiBindings
import com.ivianuu.essentials.sample.work.workBindings
import com.ivianuu.essentials.twilight.esTwilight
import com.ivianuu.essentials.work.workerInjectionModule
import com.ivianuu.injekt.ComponentBuilder

class App : EsApp() {

    override fun ComponentBuilder.buildComponent() {
        esAppsCoilBindings()
        esBilling()
        esBootInjection()
        esCoil()
        esGestures()
        esPermissions()
        esTwilight()
        workerInjectionModule()

        appBindings()
        bootBindings()
        uiBindings()
        workBindings()
    }

}