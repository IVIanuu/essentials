/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

@file:Suppress("UNCHECKED_CAST")

package com.ivianuu.essentials.di

import java.lang.IllegalStateException

class Container internal constructor(
  private val parent: Container?,
  @PublishedApi internal val factories: Map<TypeKey<*>, List<Container.() -> Any?>>
) {
  @PublishedApi internal fun <T> getFactoryOrNull(key: TypeKey<T>): (Container.() -> Any?)? {
    when (key.classifierFqName) {
      "kotlin.collections.List" -> {
        val elementKey = key.arguments[0]
        return {
          factories[elementKey]
            ?.map { it(this) }
            ?.let { it as List<T> }
        }
      }
      "kotlin.Function0" -> {
        val valueKey = key.arguments[0]
        return {
          {
            get(valueKey)
          }
        }
      }
      "kotlin.Function1" -> {
        val parameter1Key = key.arguments[0] as TypeKey<Any?>
        val valueKey = key.arguments[1]
        return {
          { p1: Any? ->
            buildChildContainer {
              add(parameter1Key) { p1 }
            }.get(valueKey)
          }
        }
      }
      "kotlin.Function2" -> {
        val parameter1Key = key.arguments[0] as TypeKey<Any?>
        val parameter2Key = key.arguments[1] as TypeKey<Any?>
        val valueKey = key.arguments[2]
        return {
          { p1: Any?, p2: Any? ->
            buildChildContainer {
              add(parameter1Key) { p1 }
              add(parameter2Key) { p2 }
            }.get(valueKey)
          }
        }
      }
      "kotlin.Function3" -> {
        val parameter1Key = key.arguments[0] as TypeKey<Any?>
        val parameter2Key = key.arguments[1] as TypeKey<Any?>
        val parameter3Key = key.arguments[2] as TypeKey<Any?>
        val valueKey = key.arguments[3]
        return {
          { p1: Any?, p2: Any?, p3: Any? ->
            buildChildContainer {
              add(parameter1Key) { p1 }
              add(parameter2Key) { p2 }
              add(parameter3Key) { p3 }
            }.get(valueKey)
          }
        }
      }
      "kotlin.Function4" -> {
        val parameter1Key = key.arguments[0] as TypeKey<Any?>
        val parameter2Key = key.arguments[1] as TypeKey<Any?>
        val parameter3Key = key.arguments[2] as TypeKey<Any?>
        val parameter4Key = key.arguments[3] as TypeKey<Any?>
        val valueKey = key.arguments[4]
        return {
          { p1: Any?, p2: Any?, p3: Any?, p4: Any? ->
            buildChildContainer {
              add(parameter1Key) { p1 }
              add(parameter2Key) { p2 }
              add(parameter3Key) { p3 }
              add(parameter4Key) { p4 }
            }.get(valueKey)
          }
        }
      }
      "kotlin.Function5" -> {
        val parameter1Key = key.arguments[0] as TypeKey<Any?>
        val parameter2Key = key.arguments[1] as TypeKey<Any?>
        val parameter3Key = key.arguments[2] as TypeKey<Any?>
        val parameter4Key = key.arguments[3] as TypeKey<Any?>
        val parameter5Key = key.arguments[4] as TypeKey<Any?>
        val valueKey = key.arguments[5]
        return {
          { p1: Any?, p2: Any?, p3: Any?, p4: Any?, p5: Any? ->
            buildChildContainer {
              add(parameter1Key) { p1 }
              add(parameter2Key) { p2 }
              add(parameter3Key) { p3 }
              add(parameter4Key) { p4 }
              add(parameter5Key) { p5 }
            }.get(valueKey)
          }
        }
      }
      "kotlin.Function6" -> {
        val parameter1Key = key.arguments[0] as TypeKey<Any?>
        val parameter2Key = key.arguments[1] as TypeKey<Any?>
        val parameter3Key = key.arguments[2] as TypeKey<Any?>
        val parameter4Key = key.arguments[3] as TypeKey<Any?>
        val parameter5Key = key.arguments[4] as TypeKey<Any?>
        val parameter6Key = key.arguments[5] as TypeKey<Any?>
        val valueKey = key.arguments[6]
        return {
          { p1: Any?, p2: Any?, p3: Any?, p4: Any?, p5: Any?, p6: Any? ->
            buildChildContainer {
              add(parameter1Key) { p1 }
              add(parameter2Key) { p2 }
              add(parameter3Key) { p3 }
              add(parameter4Key) { p4 }
              add(parameter5Key) { p5 }
              add(parameter6Key) { p6 }
            }.get(valueKey)
          }
        }
      }
      else -> {
        val factoriesForKey = factories[key] ?: return parent?.getFactoryOrNull(key)
        return factoriesForKey.singleOrNull() ?: throw MultipleInjectablesException(key)
      }
    }

  }

  class Builder @PublishedApi internal constructor(private val parent: Container?) {
    private val factories = mutableMapOf<TypeKey<*>, MutableList<Container.() -> Any?>>()

    fun <T> add(key: TypeKey<T>, factory: Container.() -> T) {
      factories.getOrPut(key) { mutableListOf() } += factory
    }

    fun build(): Container = Container(parent, factories)
  }
}

inline fun buildContainer(builder: Container.Builder.() -> Unit): Container =
  Container.Builder(null).apply(builder).build()

inline fun Container.buildChildContainer(builder: Container.Builder.() -> Unit): Container =
  Container.Builder(this).apply(builder).build()

inline fun <reified T> Container.getOrElse(default: () -> T): T =
  getOrElse(typeKeyOf(), default)

inline fun <T> Container.getOrElse(key: TypeKey<T>, default: () -> T): T =
  getFactoryOrNull(key)?.let { it(this) as T } ?: default()

inline fun <reified T> Container.get(): T = get(typeKeyOf())

fun <T> Container.get(key: TypeKey<T>): T = getOrElse(key) { throw NoInjectableFoundException(key) }

inline fun <reified T> Container.Builder.add(noinline factory: Container.() -> T) =
  add(typeKeyOf(), factory)

inline fun <reified T : Any, reified S> Container.Builder.addScoped(
  scope: S,
  noinline factory: Container.() -> T
) = addScoped(typeKeyOf(), typeKeyOf<S>(), factory)

inline fun <reified T> Container.resolve(func: () -> T) = func()

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

fun <T : Any, S> Container.Builder.addScoped(
  key: TypeKey<T>,
  scopeKey: TypeKey<S>,
  factory: Container.() -> T
) = add(key) {
  val scope = get(
    typeKeyOf<Scope<S>>(
      classifierFqName = Scope::class.qualifiedName!!,
      arguments = arrayOf(scopeKey)
    )
  )

  scope(key) { factory() }
}

class NoInjectableFoundException(
  val key: TypeKey<*>,
  override val message: String? = "No injectable found for $key"
) : IllegalStateException(message)

class MultipleInjectablesException(
  val key: TypeKey<*>,
  override val message: String? = "Multiple injectables found for $key"
) : IllegalStateException(message)
