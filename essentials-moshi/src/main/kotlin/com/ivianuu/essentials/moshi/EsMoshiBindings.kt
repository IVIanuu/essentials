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

import com.ivianuu.essentials.sourcekey.memo
import com.ivianuu.injekt.*
import com.ivianuu.injekt.component.ApplicationScoped
import com.ivianuu.injekt.component.Storage
import com.squareup.moshi.Moshi

fun <T : Any> jsonAdapterBinding(): @GivenSetElement (@Given T) -> JsonAdapter = { it }

typealias JsonAdapter = Any

@Given
fun moshi(
    @Given jsonAdapters: Set<JsonAdapter>,
    @Given storage: Storage<ApplicationScoped>
): Moshi = storage.memo {
    Moshi.Builder()
        .apply {
            jsonAdapters
                .forEach { adapter -> add(adapter) }
        }
        .build()
}

@Given
inline fun <reified T> @Given Moshi.jsonAdapter(): com.squareup.moshi.JsonAdapter<T> = adapter()
