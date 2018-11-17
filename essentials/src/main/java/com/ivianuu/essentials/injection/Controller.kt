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

import com.ivianuu.director.Controller
import com.ivianuu.injectors.HasInjectors
import com.ivianuu.injectors.Injector
import com.ivianuu.injectors.inject

private val INJECTORS_FINDER: (Controller) -> HasInjectors? = {
    var hasInjectors: HasInjectors? = null

    var parentController = it.parentController

    // loop trough parents
    while (parentController != null) {
        if (parentController is HasInjectors) {
            hasInjectors = parentController
            break
        }

        parentController = parentController.parentController
    }

    // check activity
    if (hasInjectors == null) {
        hasInjectors = it.activity as? HasInjectors
    }

    // check application
    if (hasInjectors == null) {
        hasInjectors = it.activity.application as? HasInjectors
    }

    hasInjectors
}

fun Injector.Companion.inject(instance: Controller) {
    inject(instance, INJECTORS_FINDER)
}

fun Controller.inject() = Injector.inject(this)