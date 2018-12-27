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
import com.ivianuu.essentials.app.EsApp
import com.ivianuu.essentials.util.ext.unsafeLazy
import com.ivianuu.injectors.Injector
import com.ivianuu.injekt.*
import com.ivianuu.timberktx.d
import com.ivianuu.timberktx.e
import com.ivianuu.timberktx.i
import com.ivianuu.timberktx.w

/**
 * App
 */
class App : EsApp(), ComponentHolder {

    override val component by unsafeLazy {
        component(
            module {
                factory<Application> { this@App }
                single("username") { "Manuel" }
            }
        )
    }

    override fun applicationInjector(): Injector<out EsApp> =
        DaggerAppComponent.builder().create(this)

    override fun onCreate() {
        super.onCreate()
        InjektPlugins.logger = object : InjektPlugins.Logger {
            override fun debug(msg: String) {
                d { msg }
            }

            override fun info(msg: String) {
                i { msg }
            }

            override fun warn(msg: String) {
                w { msg }
            }

            override fun error(msg: String, throwable: Throwable?) {
                e(throwable) { msg }
            }

        }
    }

}