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

package com.ivianuu.essentials.ui.navigation.director

import android.app.Application
import androidx.fragment.app.FragmentActivity
import com.ivianuu.director.Controller
import com.ivianuu.director.ControllerChangeHandler
import com.ivianuu.essentials.ui.navigation.Route
import com.ivianuu.essentials.util.Properties
import com.ivianuu.injekt.InjektTrait
import com.ivianuu.injekt.ParametersDefinition
import com.ivianuu.injekt.Type
import com.ivianuu.injekt.get
import com.ivianuu.injekt.typeOf

class ControllerRoute(
    val type: Type<out Controller>,
    val name: String? = null,
    val extras: Properties = Properties(),
    val options: Options? = null,
    val factory: (Context) -> Controller
) : Route {

    class Context(
        val parent: Controller?,
        val activity: FragmentActivity,
        val application: Application
    )

    class Options {
        private var pushHandler: ControllerChangeHandler? = null
        private var popHandler: ControllerChangeHandler? = null

        fun pushHandler(): ControllerChangeHandler? = pushHandler

        fun pushHandler(handler: ControllerChangeHandler?) = apply {
            pushHandler = handler
        }

        fun popHandler(): ControllerChangeHandler? = popHandler

        fun popHandler(handler: ControllerChangeHandler?) = apply {
            popHandler = handler
        }

    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ControllerRoute

        if (type != other.type) return false
        if (name != other.name) return false

        return true
    }

    override fun hashCode(): Int {
        var result = type.hashCode()
        result = 31 * result + (name?.hashCode() ?: 0)
        return result
    }
}

inline fun <reified T : Controller> controllerRoute(
    name: String? = null,
    extras: Properties = Properties(),
    options: ControllerRoute.Options? = null,
    noinline parameters: ParametersDefinition? = null
) = controllerRoute(typeOf<T>(), name, extras, options, parameters)

fun <T : Controller> controllerRoute(
    type: Type<T>,
    name: String? = null,
    extras: Properties = Properties(),
    options: ControllerRoute.Options? = null,
    parameters: ParametersDefinition? = null
) = controllerRoute(type, name, extras, options) { context ->
    val injektTrait = context.parent as? InjektTrait
        ?: context.activity as? InjektTrait
        ?: context.application as? InjektTrait
        ?: error("couldn't find injekt trait")

    injektTrait.get(type, parameters = parameters)
}

fun controllerRoute(
    type: Type<out Controller>,
    name: String? = null,
    extras: Properties = Properties(),
    options: ControllerRoute.Options? = null,
    factory: (ControllerRoute.Context) -> Controller
): ControllerRoute = ControllerRoute(type, name, extras, options, factory)

fun ControllerRoute.copy(
    type: Type<out Controller> = this.type,
    name: String? = this.name,
    extras: Properties = this.extras,
    options: ControllerRoute.Options? = this.options,
    factory: (ControllerRoute.Context) -> Controller = this.factory
) = ControllerRoute(type, name, extras, options, factory)

