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

package com.ivianuu.essentials.coil

import coil.CoilAccessor
import coil.ImageLoader
import coil.decode.Decoder
import com.ivianuu.essentials.app.androidApplicationContext
import com.ivianuu.injekt.ApplicationContext
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.GivenSetElements
import com.ivianuu.injekt.given

object EsCoilModule {

    @Given(ApplicationContext::class)
    fun imageLoader() = ImageLoader.Builder(androidApplicationContext)
        .componentRegistry {
            given<Set<Decoder>>().forEach { add(it) }
            given<Set<FetcherBinding<*>>>()
                .forEach { binding ->
                    CoilAccessor.add(this, binding.type.java, binding.fetcher)
                }
            given<Set<MapperBinding<*>>>()
                .forEach { binding ->
                    CoilAccessor.add(this, binding.type.java, binding.mapper)
                }
            given<Set<MeasuredMapperBinding<*>>>()
                .forEach { binding ->
                    CoilAccessor.add(this, binding.type.java, binding.mapper)
                }
        }
        .build()

    @GivenSetElements
    fun decoders(): Set<Decoder> = emptySet()

    @GivenSetElements
    fun fetchers(): Set<FetcherBinding<*>> = emptySet()

    @GivenSetElements
    fun mappers(): Set<MapperBinding<*>> = emptySet()

    @GivenSetElements
    fun measuredMappers(): Set<MeasuredMapperBinding<*>> = emptySet()

}
