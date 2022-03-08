/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

@file:Suppress("UNCHECKED_CAST")

package com.ivianuu.essentials.di

interface Container {
  fun <T> getFactory(key: TypeKey<T>): List<Container.() -> T>
}

interface ContainerBuilder {
  fun <T> add(key: TypeKey<T>, factory: Container.() -> T)

  fun build(): Container
}

@PublishedApi internal class ContainerImpl(
  private val parent: Container?,
  private val factories: Map<TypeKey<*>, List<Container.() -> Any?>>
) : Container {
  override fun <T> getFactory(key: TypeKey<T>): List<Container.() -> T> =


  class Builder @PublishedApi internal constructor(private val parent: Container?) : ContainerBuilder {
    private val factories = mutableMapOf<TypeKey<*>, MutableList<Container.() -> Any?>>()
    
    override fun <T> add(key: TypeKey<T>, factory: Container.() -> T) {
      factories.getOrPut(key) { mutableListOf() } += factory as Container.() -> Any?
    }

    override fun build(): Container = ContainerImpl(parent, factories)
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

class NoInjectableFoundException(
  val key: TypeKey<*>,
  override val message: String? = "No injectable found for $key"
) : IllegalStateException(message)

class MultipleInjectablesException(
  val key: TypeKey<*>,
  override val message: String? = "Multiple injectables found for $key"
) : IllegalStateException(message)
