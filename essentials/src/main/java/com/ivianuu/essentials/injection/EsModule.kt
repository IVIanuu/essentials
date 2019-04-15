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

import com.ivianuu.essentials.app.appInitializerInjectionModule
import com.ivianuu.essentials.app.appServiceInjectionModule
import com.ivianuu.essentials.app.esAppInitializersModule
import com.ivianuu.essentials.app.esAppServicesModule
import com.ivianuu.essentials.ui.traveler.travelerModule
import com.ivianuu.essentials.util.esUtilModule
import com.ivianuu.injekt.module

/**
 * Core modules
 */
val esModule = module {
    module(appInitializerInjectionModule)
    module(appServiceInjectionModule)
    module(esAppInitializersModule)
    module(esAppServicesModule)
    module(esUtilModule)
    module(systemServiceModule)
    module(travelerModule)
}