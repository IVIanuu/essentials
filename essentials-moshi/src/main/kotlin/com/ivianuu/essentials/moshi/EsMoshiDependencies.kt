/*
 * Copyright 2020 Manuel Wrage
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

package com.ivianuu.essentials.moshi

import com.ivianuu.injekt.Binding
import com.ivianuu.injekt.BindingAdapter
import com.ivianuu.injekt.SetElements
import com.ivianuu.injekt.merge.ApplicationComponent
import com.squareup.moshi.Moshi

@BindingAdapter
annotation class JsonAdapterBinding {
    companion object {
        @SetElements
        fun <T : Any> invoke(instance: T): JsonAdapters = setOf(instance)
    }
}

typealias JsonAdapters = Set<Any>

@Binding(ApplicationComponent::class)
fun moshi(jsonAdapters: JsonAdapters): Moshi = Moshi.Builder()
    .apply {
        jsonAdapters
            .forEach { adapter -> add(adapter) }
    }
    .build()

@SetElements
fun defaultAdapters(): JsonAdapters = emptySet()
