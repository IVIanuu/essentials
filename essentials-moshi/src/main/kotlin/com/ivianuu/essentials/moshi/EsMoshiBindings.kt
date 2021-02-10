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

import com.ivianuu.injekt.*
import com.ivianuu.injekt.common.Scoped
import com.ivianuu.injekt.component.AppComponent
import com.squareup.moshi.Moshi

@Qualifier annotation class JsonAdapterBinding

@Macro
@GivenSetElement
fun <T : @JsonAdapterBinding Any> jsonAdapterImpl(@Given instance: T): JsonAdapter = instance

typealias JsonAdapter = Any

@Scoped<AppComponent>
@Given
fun moshi(@Given jsonAdapters: Set<JsonAdapter>): Moshi = Moshi.Builder()
    .apply {
        jsonAdapters
            .forEach { adapter -> add(adapter) }
    }
    .build()!!

@Given inline fun <reified T> @Given Moshi.jsonAdapter(): com.squareup.moshi.JsonAdapter<T> =
    adapter()
