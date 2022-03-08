/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

@file:Suppress("UNCHECKED_CAST")

package com.ivianuu.essentials.di

interface Container {
  fun <T> getFactories(key: TypeKey<T>): List<Container.() -> Any?>
}

interface ContainerBuilder {
  fun addProvider(factoryProvider: (TypeKey<*>) -> (Container.() -> Any?)?)

  fun <T> add(key: TypeKey<T>, factory: Container.() -> T)

  fun build(): Container
}

@PublishedApi internal class ContainerImpl(
  private val parent: Container?,
  private val factoryProviders: List<(TypeKey<*>) -> (Container.() -> Any?)?>,
  private val factories: Map<TypeKey<*>, List<Container.() -> Any?>>
) : Container {
  override fun <T> getFactories(key: TypeKey<T>) = buildList<Container.() -> Any?> {
    factories[key] =
  }

  class Builder @PublishedApi internal constructor(private val parent: Container?) : ContainerBuilder {
    private val factoryProviders = mutableListOf<(TypeKey<*>) -> (Container.() -> Any?)?>()
    private val factories = mutableMapOf<TypeKey<*>, MutableList<Container.() -> Any?>>()

    override fun addProvider(factoryProvider: (TypeKey<*>) -> (Container.() -> Any?)?) {
      
    }
    
    override fun <T> add(key: TypeKey<T>, factory: Container.() -> T) {
      factories.getOrPut(key) { mutableListOf() } += factory as Container.() -> Any?
    }

    override fun build(): Container = ContainerImpl(parent, factoryProviders, factories)
  }
}

inline fun buildContainer(builder: ContainerBuilder.() -> Unit): Container =
  ContainerImpl.Builder(null).apply(builder).build()

inline fun Container.buildChildContainer(builder: ContainerBuilder.() -> Unit): Container =
  ContainerImpl.Builder(this).apply(builder).build()

inline fun <reified T> Container.get(): T = get(typeKeyOf())

fun <T> Container.get(key: TypeKey<T>): T {
  val factories = getFactories(key)
    .takeIf { it.isNotEmpty() } ?: throw NoInjectableFoundException(key)
  val factory = factories.singleOrNull() ?: throw MultipleInjectablesException(key)
  return factory(this) as T
}

inline fun module(noinline block: ContainerBuilder.() -> Unit) = block

inline fun <reified T> ContainerBuilder.add(noinline factory: Container.() -> T) =
  add(typeKeyOf(), factory)

inline fun <reified T> ContainerBuilder.addClassifierProvider(
  crossinline factoryProvider: Container.(TypeKey<T>) -> T
) {
  addClassifierProvider(typeKeyOf<T>().classifierFqName, factoryProvider)
}

inline fun <T> ContainerBuilder.addClassifierProvider(
  classifierFqName: String,
  crossinline factoryProvider: Container.(TypeKey<T>) -> T
) {
  addProvider { key ->
    if (key.classifierFqName != classifierFqName) null
    else ({ factoryProvider(key as TypeKey<T>) })
  }
}

inline fun <reified P1, reified T> Container.resolve(func: (P1) -> T) = func(get())

inline fun <reified P1, reified P2, reified T> Container.resolve(func: (P1, P2) -> T) =
  func(get(), get())

inline fun <reified P1, reified P2, reified P3, reified T> Container.resolve(func: (P1, P2, P3) -> T) =
  func(get(), get(), get())

inline fun <reified P1, reified P2, reified P3, reified P4, reified T> Container.resolve(func: (P1, P2, P3, P4) -> T) =
  func(get(), get(), get(), get())

inline fun <reified P1, reified P2, reified P3, reified P4, reified P5, reified T> Container.resolve(func: (P1, P2, P3, P4, P5) -> T) =
  func(get(), get(), get(), get(), get())

inline fun <reified P1, reified P2, reified P3, reified P4, reified P5, reified P6, reified T> Container.resolve(func: (P1, P2, P3, P4, P5, P6) -> T) =
  func(get(), get(), get(), get(), get(), get())

inline fun <reified P1, reified P2, reified P3, reified P4, reified P5, reified P6, reified P7, reified T> Container.resolve(func: (P1, P2, P3, P4, P5, P6, P7) -> T) =
  func(get(), get(), get(), get(), get(), get(), get())

inline fun <reified P1, reified P2, reified P3, reified P4, reified P5, reified P6, reified P7, reified P8, reified T> Container.resolve(func: (P1, P2, P3, P4, P5, P6, P7, P8) -> T) =
  func(get(), get(), get(), get(), get(), get(), get(), get())

inline fun <reified P1, reified P2, reified P3, reified P4, reified P5, reified P6, reified P7, reified P8, reified P9, reified T> Container.resolve(func: (P1, P2, P3, P4, P5, P6, P7, P8, P9) -> T) =
  func(get(), get(), get(), get(), get(), get(), get(), get(), get())

inline fun <reified P1, reified P2, reified P3, reified P4, reified P5, reified P6, reified P7, reified P8, reified P9, reified P10, reified T> Container.resolve(func: (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10) -> T) =
  func(get(), get(), get(), get(), get(), get(), get(), get(), get(), get())

inline fun <reified P1, reified P2, reified P3, reified P4, reified P5, reified P6, reified P7, reified P8, reified P9, reified P10, reified P11, reified T> Container.resolve(func: (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11) -> T) =
  func(get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get())

inline fun <reified P1, reified P2, reified P3, reified P4, reified P5, reified P6, reified P7, reified P8, reified P9, reified P10, reified P11, reified P12, reified T> Container.resolve(func: (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12) -> T) =
  func(get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get())

inline fun <reified P1, reified P2, reified P3, reified P4, reified P5, reified P6, reified P7, reified P8, reified P9, reified P10, reified P11, reified P12, reified P13, reified T> Container.resolve(func: (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13) -> T) =
  func(get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get())

inline fun <reified P1, reified P2, reified P3, reified P4, reified P5, reified P6, reified P7, reified P8, reified P9, reified P10, reified P11, reified P12, reified P13, reified P14, reified T> Container.resolve(func: (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14) -> T) =
  func(get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get())

inline fun <reified P1, reified P2, reified P3, reified P4, reified P5, reified P6, reified P7, reified P8, reified P9, reified P10, reified P11, reified P12, reified P13, reified P14, reified P15, reified T> Container.resolve(func: (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15) -> T) =
  func(get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get())

inline fun <reified P1, reified P2, reified P3, reified P4, reified P5, reified P6, reified P7, reified P8, reified P9, reified P10, reified P11, reified P12, reified P13, reified P14, reified P15, reified P16, reified T> Container.resolve(func: (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16) -> T) =
  func(get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get())

inline fun <reified P1, reified P2, reified P3, reified P4, reified P5, reified P6, reified P7, reified P8, reified P9, reified P10, reified P11, reified P12, reified P13, reified P14, reified P15, reified P16, reified P17, reified T> Container.resolve(func: (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17) -> T) =
  func(get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get())

inline fun <reified P1, reified P2, reified P3, reified P4, reified P5, reified P6, reified P7, reified P8, reified P9, reified P10, reified P11, reified P12, reified P13, reified P14, reified P15, reified P16, reified P17, reified P18, reified T> Container.resolve(func: (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18) -> T) =
  func(get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get())

inline fun <reified P1, reified P2, reified P3, reified P4, reified P5, reified P6, reified P7, reified P8, reified P9, reified P10, reified P11, reified P12, reified P13, reified P14, reified P15, reified P16, reified P17, reified P18, reified P19, reified T> Container.resolve(func: (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19) -> T) =
  func(get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get())

inline fun <reified P1, reified P2, reified P3, reified P4, reified P5, reified P6, reified P7, reified P8, reified P9, reified P10, reified P11, reified P12, reified P13, reified P14, reified P15, reified P16, reified P17, reified P18, reified P19, reified P20, reified T> Container.resolve(func: (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20) -> T) =
  func(get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get())

inline fun <reified P1, reified P2, reified P3, reified P4, reified P5, reified P6, reified P7, reified P8, reified P9, reified P10, reified P11, reified P12, reified P13, reified P14, reified P15, reified P16, reified P17, reified P18, reified P19, reified P20, reified P21, reified T> Container.resolve(func: (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21) -> T) =
  func(get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get())

class NoInjectableFoundException(
  val key: TypeKey<*>,
  override val message: String? = "No injectable found for $key"
) : IllegalStateException(message)

class MultipleInjectablesException(
  val key: TypeKey<*>,
  override val message: String? = "Multiple injectables found for $key"
) : IllegalStateException(message)
