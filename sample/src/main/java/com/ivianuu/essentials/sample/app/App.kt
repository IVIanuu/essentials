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

import com.ivianuu.essentials.app.EsApp
import com.ivianuu.essentials.app.esAppsModule
import com.ivianuu.essentials.app.glide.esAppGlideModule
import com.ivianuu.essentials.hidenavbar.esNavBarModule
import com.ivianuu.essentials.sample.injekt.autoModule
import com.ivianuu.essentials.shell.esShellModule

/**
 * App
 */
class App : EsApp() {

    override fun modules() = listOf(
        autoModule,
        esAppsModule,
        esAppGlideModule,
        esNavBarModule,
        esShellModule
    )


}