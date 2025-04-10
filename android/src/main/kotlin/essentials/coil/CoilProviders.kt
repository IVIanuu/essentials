/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.coil

import android.app.*
import androidx.compose.ui.util.*
import coil.*
import coil.decode.*
import coil.fetch.*
import coil.intercept.*
import coil.key.*
import coil.map.*
import essentials.*
import essentials.coroutines.*
import injekt.*
import kotlinx.coroutines.*
import kotlin.reflect.*

@OptIn(ExperimentalStdlibApi::class)
@Provide object CoilProviders {
  data object CoilInit
  @Provide fun initializeCoil(imageLoaderFactory: () -> ImageLoader): ScopeInit<AppScope, CoilInit> {
    Coil.setImageLoader(imageLoaderFactory)
  }

  @Provide fun imageLoader(
    context: Application,
    coroutineContexts: CoroutineContexts,
    decoderFactories: List<Decoder.Factory>,
    fetcherFactories: List<FetcherFactoryBinding<*>>,
    keyers: List<KeyerBinding<*>>,
    interceptors: List<Interceptor>,
    mappers: List<MapperBinding<*>>,
  ): @Scoped<AppScope> ImageLoader = ImageLoader.Builder(context)
    .let { builder ->
      coroutineContexts.io[CoroutineDispatcher]
        ?.let { builder.dispatcher(it) }
        ?: builder
    }
    .components {
      decoderFactories.fastForEach { add(it) }
      interceptors.fastForEach { add(it) }
      keyers.fastForEach { add(it.keyer as Keyer<Any>, it.type.java as Class<Any>) }
      fetcherFactories
        .fastForEach { add(it.factory as Fetcher.Factory<Any>, it.type.java as Class<Any>) }
      mappers
        .fastForEach { add(it.mapper as Mapper<Any, Any>, it.type.java as Class<Any>) }
    }
    .build()

  @Provide val defaultDecoderFactories get() = emptyList<Decoder.Factory>()

  @Provide val defaultFetcherFactoryBindings get() = emptyList<FetcherFactoryBinding<*>>()

  @Provide fun <@AddOn F : Fetcher.Factory<T>, T : Any> fetcherBinding(
    type: KClass<T>,
    instance: F
  ): FetcherFactoryBinding<*> = FetcherFactoryBinding(type, instance)

  data class FetcherFactoryBinding<T : Any>(
    val type: KClass<T>,
    val factory: Fetcher.Factory<T>
  )

  data class MapperBinding<T : Any>(val type: KClass<T>, val mapper: Mapper<T, *>)

  @Provide fun <@AddOn M : Mapper<T, V>, T : Any, V : Any> mapperBinding(
    type: KClass<T>,
    instance: M
  ): MapperBinding<*> = MapperBinding(type, instance)

  @Provide val defaultMappers get() = emptyList<MapperBinding<*>>()

  @Provide val defaultInterceptors get() = emptyList<Interceptor>()

  data class KeyerBinding<T : Any>(val type: KClass<T>, val keyer: Keyer<T>)

  @Provide fun <@AddOn K : Keyer<T>, T : Any> keyerBinding(
    type: KClass<T>,
    instance: K
  ): KeyerBinding<*> = KeyerBinding(type, instance)

  @Provide val defaultKeyers get() = emptyList<KeyerBinding<*>>()
}
