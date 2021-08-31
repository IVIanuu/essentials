/*
 * Copyright 2021 Manuel Wrage
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

import coil.*
import coil.decode.*
import coil.fetch.*
import coil.intercept.*
import coil.map.*
import com.ivianuu.essentials.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.scope.*
import kotlin.reflect.*

@Provide fun imageLoader(
  context: AppContext,
  decoders: Set<Decoder> = emptySet(),
  fetchers: Set<FetcherPair<*>> = emptySet(),
  interceptors: Set<Interceptor> = emptySet(),
  mappers: Set<MapperPair<*>> = emptySet(),
): @Scoped<AppScope> ImageLoader = ImageLoader.Builder(context)
  .componentRegistry {
    decoders.forEach { add(it) }
    interceptors.forEach { add(it) }
    fetchers
      .forEach { binding ->
        CoilAccessor.add(this, binding.type.java, binding.fetcher)
      }
    mappers
      .forEach { binding ->
        CoilAccessor.add(this, binding.type.java, binding.mapper)
      }
  }
  .build()

@Provide fun <@Spread F : Fetcher<T>, T : Any> fetcherPair(
  instance: F,
  typeClass: KClass<T>
): FetcherPair<*> = FetcherPair(instance, typeClass)

data class FetcherPair<T : Any>(
  val fetcher: Fetcher<T>,
  val type: KClass<T>
)

@Provide fun <@Spread M : Mapper<T, V>, T : Any, V : Any> mapperPair(
  instance: M,
  typeClass: KClass<T>
): MapperPair<*> = MapperPair(instance, typeClass)

data class MapperPair<T : Any>(
  val mapper: Mapper<T, *>,
  val type: KClass<T>
)
