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

package com.ivianuu.essentials.util.ext

import com.airbnb.epoxy.EpoxyController
import com.airbnb.epoxy.TypedEpoxyController

fun <T> typedEpoxyController(buildModels: TypedEpoxyController<T>.(data: T) -> Unit): TypedEpoxyController<T> {
    return object : TypedEpoxyController<T>() {
        override fun buildModels(data: T) {
            buildModels.invoke(this, data)
        }
    }
}

fun <T> listEpoxyController(buildModel: EpoxyController.(item: T) -> Unit): EpoxyController {
    return typedEpoxyController<List<T>> { it.forEach { buildModel.invoke(this, it) } }
}