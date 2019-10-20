package com.ivianuu.essentials.ui.coil

import coil.CoilAccessor
import coil.ImageLoader
import coil.decode.Decoder
import coil.fetch.Fetcher
import com.github.ajalt.timberkt.d
import com.ivianuu.injekt.*
import kotlin.reflect.KClass

val esCoilModule = module {
    set<Decoder>(Decoders)
    set<FetcherBinding<*>>(Fetchers)
    /*set<Pair<KClass<*>, Mapper<*, *>>>(Mappers)
    set<Pair<KClass<*>, MeasuredMapper<*, *>>>(MeasuredMappers)*/ // todo

    single {
        ImageLoader(get()) {
            componentRegistry {
                d { "init image loader" }
                get<Set<Decoder>>(Decoders)
                    .forEach {
                        add(it)
                    }

                get<Set<FetcherBinding<*>>>(Fetchers)
                    .forEach { binding ->
                        d { "add fetcher $binding" }
                        CoilAccessor.add(this, binding.type.java, binding.fetcher)
                    }

                /*get<Set<Pair<*, Mapper<*, *>>>>(Mappers)
                    .forEach { (type, mapper) ->
                        CoilAccessor.add(this, (type as KClass<Any>).java, mapper)
                    }

                get<Set<Pair<*, MeasuredMapper<*, *>>>>(MeasuredMappers)
                    .forEach { (type, mapper) ->
                        CoilAccessor.add(this, (type as KClass<Any>).java, mapper)
                    }*/ // todo
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
    println("bind fetcher f ${F::class}, t ${T::class}")
    module.factory<FetcherBinding<T>> {
        println("const f ${F::class}, t ${T::class}")
        FetcherBinding<T>(fetcher = get<F>(), type = type).also {
            println("const done $it")
        }
    }.also { bbc ->
        println("bbc key ${bbc.key}")
        module.set<FetcherBinding<*>>(Fetchers) {
            add(bbc.key, false)
        }
    }

    return this
}