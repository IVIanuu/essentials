package com.ivianuu.essentials.ui.coil

import coil.CoilAccessor
import coil.ImageLoader
import coil.decode.Decoder
import coil.fetch.Fetcher
import coil.map.Mapper
import coil.map.MeasuredMapper
import com.ivianuu.injekt.*
import kotlin.reflect.KClass

val esCoilModule = module {
    set<Decoder>(Decoders)
    set<FetcherBinding<*>>(Fetchers)
    set<MapperBinding<*>>(Mappers)
    set<MeasuredMapperBinding<*>>(MeasuredMappers)

    single {
        ImageLoader(get()) {
            componentRegistry {
                get<Set<Decoder>>(Decoders)
                    .forEach {
                        add(it)
                    }

                get<Set<FetcherBinding<*>>>(Fetchers)
                    .forEach { binding ->
                        CoilAccessor.add(this, binding.type.java, binding.fetcher)
                    }

                get<Set<MapperBinding<*>>>(Mappers)
                    .forEach { binding ->
                        CoilAccessor.add(this, binding.type.java, binding.mapper)
                    }

                get<Set<MeasuredMapperBinding<*>>>(MeasuredMappers)
                    .forEach { binding ->
                        CoilAccessor.add(this, binding.type.java, binding.mapper)
                    }
            }
        }
    }
}

@Name(Decoders.Companion::class)
annotation class Decoders {
    companion object
}

inline fun <reified T : Decoder> Module.decoder(
    name: Any? = null,
    noinline definition: Definition<T>
): BindingContext<T> = factory(name = name, definition = definition).bindDecoder()

inline fun <reified T : Decoder> Module.bindDecoder(
    name: Any? = null
) {
    withBinding<T>(name) { bindDecoder() }
}

inline fun <reified T : Decoder> BindingContext<T>.bindDecoder(): BindingContext<T> {
    intoSet<T, Decoder>(Decoders)
    return this
}

@Name(Fetchers.Companion::class)
annotation class Fetchers {
    companion object
}

data class FetcherBinding<T : Any>(
    val fetcher: Fetcher<T>,
    val type: KClass<T>
)

// todo remove type arg once fixed

inline fun <reified F : Fetcher<T>, reified T : Any> Module.fetcher(
    type: KClass<T>,
    name: Any? = null,
    noinline definition: Definition<F>
): BindingContext<F> = factory(name = name, definition = definition).bindFetcher<F, T>(type)

inline fun <reified F : Fetcher<T>, reified T : Any> Module.bindFetcher(
    type: KClass<T>,
    name: Any? = null
) {
    withBinding<F>(name) { bindFetcher<F, T>(type) }
}

inline fun <reified F : Fetcher<T>, reified T : Any> BindingContext<F>.bindFetcher(type: KClass<T>): BindingContext<F> {
    module.factory<FetcherBinding<T>> {
        FetcherBinding<T>(fetcher = get<F>(), type = type).also {
        }
    }.also { bbc ->
        module.set<FetcherBinding<*>>(Fetchers) {
            add(bbc.key, false)
        }
    }

    return this
}

@Name(Mappers.Companion::class)
annotation class Mappers {
    companion object
}

data class MapperBinding<T : Any>(
    val mapper: Mapper<T, *>,
    val type: KClass<T>
)

// todo remove type arg once fixed

inline fun <reified M : Mapper<T, *>, reified T : Any> Module.mapper(
    type: KClass<T>,
    name: Any? = null,
    noinline definition: Definition<M>
): BindingContext<M> = factory(name = name, definition = definition).bindMapper<M, T>(type)

inline fun <reified M : Mapper<T, *>, reified T : Any> Module.bindMapper(
    type: KClass<T>,
    name: Any? = null
) {
    withBinding<M>(name) { bindMapper<M, T>(type) }
}

inline fun <reified M : Mapper<T, *>, reified T : Any> BindingContext<M>.bindMapper(type: KClass<T>): BindingContext<M> {
    module.factory<MapperBinding<T>> {
        MapperBinding<T>(mapper = get<M>(), type = type)
    }.also { bbc ->
        module.set<MapperBinding<*>>(Mappers) {
            add(bbc.key, false)
        }

        return this
    }
}


@Name(MeasuredMappers.Companion::class)
annotation class MeasuredMappers {
    companion object
}

data class MeasuredMapperBinding<T : Any>(
    val mapper: MeasuredMapper<T, *>,
    val type: KClass<T>
)

// todo remove type arg once fixed

inline fun <reified M : MeasuredMapper<T, *>, reified T : Any> Module.measuredMapper(
    type: KClass<T>,
    name: Any? = null,
    noinline definition: Definition<M>
): BindingContext<M> = factory(name = name, definition = definition).bindMeasuredMapper<M, T>(type)

inline fun <reified M : MeasuredMapper<T, *>, reified T : Any> Module.bindMeasuredMapper(
    type: KClass<T>,
    name: Any? = null
) {
    withBinding<M>(name) { bindMeasuredMapper<M, T>(type) }
}

inline fun <reified M : MeasuredMapper<T, *>, reified T : Any> BindingContext<M>.bindMeasuredMapper(
    type: KClass<T>
): BindingContext<M> {
    module.factory<MeasuredMapperBinding<T>> {
        MeasuredMapperBinding<T>(mapper = get<M>(), type = type)
    }.also { bbc ->
        module.set<MeasuredMapperBinding<*>>(MeasuredMappers) {
            add(bbc.key, false)
        }
    }

    return this
}
