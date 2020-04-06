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
import com.ivianuu.injekt.ApplicationScope
import com.ivianuu.injekt.ComponentBuilder
import com.ivianuu.injekt.Module
import com.ivianuu.injekt.common.set
import com.ivianuu.injekt.get
import com.ivianuu.injekt.single

@ApplicationScope
@Module
private fun ComponentBuilder.esCoilModule() {
    set<Decoder>(setQualifier = Decoders)
    set<FetcherBinding<*>>(setQualifier = Fetchers)
    set<MapperBinding<*>>(setQualifier = Mappers)
    set<MeasuredMapperBinding<*>>(setQualifier = MeasuredMappers)

    single {
        ImageLoader(get()) {
            componentRegistry {
                get<Set<Decoder>>(qualifier = Decoders)
                    .forEach { add(it) }

                get<Set<FetcherBinding<*>>>(qualifier = Fetchers)
                    .forEach { binding ->
                        CoilAccessor.add(this, binding.type.java, binding.fetcher)
                    }

                get<Set<MapperBinding<*>>>(qualifier = Mappers)
                    .forEach { binding ->
                        CoilAccessor.add(this, binding.type.java, binding.mapper)
                    }

                get<Set<MeasuredMapperBinding<*>>>(qualifier = MeasuredMappers)
                    .forEach { binding ->
                        CoilAccessor.add(this, binding.type.java, binding.mapper)
                    }
            }
        }
    }
}
