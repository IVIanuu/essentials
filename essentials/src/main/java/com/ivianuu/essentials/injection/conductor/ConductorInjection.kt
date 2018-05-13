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

package com.ivianuu.essentials.injection.conductor

import com.ivianuu.conductor.Controller


/**
 * Injection for [Controller]
 */
object ConductorInjection {

    /**
     * Injects the [Controller]
     */
    fun inject(controller: Controller) {
        val hasDispatchingControllerInjector = findHasControllerInjector(controller)

        if (hasDispatchingControllerInjector == null) {
            throw IllegalStateException("no injector found for $controller")
        }
        val controllerInjector = hasDispatchingControllerInjector.controllerInjector()
        controllerInjector.inject(controller)
    }

    private fun findHasControllerInjector(controller: Controller): HasControllerInjector? {
        var parentController: Controller? = controller.parentController

        while (parentController != null) {
            if (parentController is HasControllerInjector) {
                return parentController
            }
            parentController = parentController.parentController
        }

        val activity = controller.activity
        if (activity is HasControllerInjector) {
            return activity
        }


        val application = activity?.application
        if (application is HasControllerInjector) {
            return application
        }

        return null
    }
}