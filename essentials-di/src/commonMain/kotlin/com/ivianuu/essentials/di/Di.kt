/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

@file:Suppress("UNCHECKED_CAST")

package com.ivianuu.essentials.di

inline fun <reified T> buildInstance(builder: ProviderRegistry.() -> Unit): T =
  buildInstance(typeKeyOf(), builder)

inline fun <T> buildInstance(
  key: TypeKey<T>,
  builder: ProviderRegistry.() -> Unit
): T = ProviderScopeImpl(null).apply(builder).get(key)

inline fun <reified T> ProviderScope.buildChildInstance(builder: ProviderRegistry.() -> Unit): T =
  buildChildInstance(typeKeyOf(), builder)

inline fun <T> ProviderScope.buildChildInstance(
  key: TypeKey<T>,
  builder: ProviderRegistry.() -> Unit
): T = ProviderScopeImpl(this).apply(builder).get(key)

inline fun <reified T> ProviderScope.get(): T = get(typeKeyOf())

fun <T> ProviderScope.get(key: TypeKey<T>): T = getProvider(key)(this)

inline fun <reified T> ProviderRegistry.provide(noinline factory: ProviderScope.() -> T) =
  provide(typeKeyOf(), factory)

inline fun <reified T> ProviderRegistry.provideIntoList(noinline factory: ProviderScope.() -> T) =
  provideIntoList(typeKeyOf(), factory)

interface ProviderScope {
  fun <T> getProvider(key: TypeKey<T>, requestingScope: ProviderScope = this): ProviderScope.() -> T

  fun <T> collectListProviders(key: TypeKey<T>, providers: MutableList<ProviderScope.() -> T>)
}

interface ProviderRegistry {
  fun <T> provide(key: TypeKey<T>, provider: ProviderScope.() -> T)

  fun <T> provideIntoList(key: TypeKey<T>, provider: ProviderScope.() -> T)
}

@PublishedApi internal class ProviderScopeImpl(
  private val parent: ProviderScope?
) : ProviderScope, ProviderRegistry {
  private val providers = mutableMapOf<TypeKey<*>, ProviderScope.() -> Any?>()
  private val listProviders = mutableMapOf<TypeKey<*>, MutableList<ProviderScope.() -> Any?>>()

  override fun <T> provide(key: TypeKey<T>, provider: ProviderScope.() -> T) {
    if (providers[key] != null)
      throw DuplicatedProviderException(key)
    providers[key] = provider
  }

  override fun <T> provideIntoList(key: TypeKey<T>, provider: ProviderScope.() -> T) {
    listProviders.getOrPut(key) { mutableListOf() } += provider
  }

  override fun <T> getProvider(
    key: TypeKey<T>,
    requestingScope: ProviderScope
  ): ProviderScope.() -> T {
    when {
      key.classifierFqName == "kotlin.collections.List" -> {
        val elementKey = key.arguments[0]
        return {
          buildList { collectListProviders(elementKey, this) }
            .map { it(this) } as T
        }
      }
      key.classifierFqName.startsWith("kotlin.Function") -> {
        when (key.classifierFqName) {
          "kotlin.Function0" -> {
            val valueKey = key.arguments[0]
            return {
              {
                get(valueKey)
              } as T
            }
          }
          "kotlin.Function1" -> {
            val p1Key = key.arguments[0] as TypeKey<Any?>
            val valueKey = key.arguments[1]
            return {
              { p1: Any? ->
                buildChildInstance(valueKey) {
                  provide(p1Key) { p1 }
                }
              } as T
            }
          }
          "kotlin.Function2" -> {
            val p1Key = key.arguments[0] as TypeKey<Any?>
            val p2Key = key.arguments[1] as TypeKey<Any?>
            val valueKey = key.arguments[2]
            return {
              { p1: Any?, p2: Any? ->
                buildChildInstance(valueKey) {
                  provide(p1Key) { p1 }
                  provide(p2Key) { p2 }
                }
              } as T
            }
          }
          "kotlin.Function3" -> {
            val p1Key = key.arguments[0] as TypeKey<Any?>
            val p2Key = key.arguments[1] as TypeKey<Any?>
            val p3Key = key.arguments[2] as TypeKey<Any?>
            val valueKey = key.arguments[3]
            return {
              { p1: Any?, p2: Any?, p3: Any? ->
                buildChildInstance(valueKey) {
                  provide(p1Key) { p1 }
                  provide(p2Key) { p2 }
                  provide(p3Key) { p3 }
                }
              } as T
            }
          }
          "kotlin.Function4" -> {
            val p1Key = key.arguments[0] as TypeKey<Any?>
            val p2Key = key.arguments[1] as TypeKey<Any?>
            val p3Key = key.arguments[2] as TypeKey<Any?>
            val p4Key = key.arguments[3] as TypeKey<Any?>
            val valueKey = key.arguments[4]
            return {
              { p1: Any?, p2: Any?, p3: Any?, p4: Any? ->
                buildChildInstance(valueKey) {
                  provide(p1Key) { p1 }
                  provide(p2Key) { p2 }
                  provide(p3Key) { p3 }
                  provide(p4Key) { p4 }
                }
              } as T
            }
          }
          "kotlin.Function5" -> {
            val p1Key = key.arguments[0] as TypeKey<Any?>
            val p2Key = key.arguments[1] as TypeKey<Any?>
            val p3Key = key.arguments[2] as TypeKey<Any?>
            val p4Key = key.arguments[3] as TypeKey<Any?>
            val p5Key = key.arguments[4] as TypeKey<Any?>
            val valueKey = key.arguments[5]
            return {
              { p1: Any?, p2: Any?, p3: Any?, p4: Any?, p5: Any? ->
                buildChildInstance(valueKey) {
                  provide(p1Key) { p1 }
                  provide(p2Key) { p2 }
                  provide(p3Key) { p3 }
                  provide(p4Key) { p4 }
                  provide(p5Key) { p5 }
                }
              } as T
            }
          }
          "kotlin.Function6" -> {
            val p1Key = key.arguments[0] as TypeKey<Any?>
            val p2Key = key.arguments[1] as TypeKey<Any?>
            val p3Key = key.arguments[2] as TypeKey<Any?>
            val p4Key = key.arguments[3] as TypeKey<Any?>
            val p5Key = key.arguments[4] as TypeKey<Any?>
            val p6Key = key.arguments[5] as TypeKey<Any?>
            val valueKey = key.arguments[6]
            return {
              { p1: Any?, p2: Any?, p3: Any?, p4: Any?, p5: Any?, p6: Any? ->
                buildChildInstance(valueKey) {
                  provide(p1Key) { p1 }
                  provide(p2Key) { p2 }
                  provide(p3Key) { p3 }
                  provide(p4Key) { p4 }
                  provide(p5Key) { p5 }
                  provide(p6Key) { p6 }
                }
              } as T
            }
          }
          "kotlin.Function7" -> {
            val p1Key = key.arguments[0] as TypeKey<Any?>
            val p2Key = key.arguments[1] as TypeKey<Any?>
            val p3Key = key.arguments[2] as TypeKey<Any?>
            val p4Key = key.arguments[3] as TypeKey<Any?>
            val p5Key = key.arguments[4] as TypeKey<Any?>
            val p6Key = key.arguments[5] as TypeKey<Any?>
            val p7Key = key.arguments[6] as TypeKey<Any?>
            val valueKey = key.arguments[7]
            return {
              { p1: Any?, p2: Any?, p3: Any?, p4: Any?, p5: Any?, p6: Any?, p7: Any? ->
                buildChildInstance(valueKey) {
                  provide(p1Key) { p1 }
                  provide(p2Key) { p2 }
                  provide(p3Key) { p3 }
                  provide(p4Key) { p4 }
                  provide(p5Key) { p5 }
                  provide(p6Key) { p6 }
                  provide(p7Key) { p7 }
                }
              } as T
            }
          }
          "kotlin.Function8" -> {
            val p1Key = key.arguments[0] as TypeKey<Any?>
            val p2Key = key.arguments[1] as TypeKey<Any?>
            val p3Key = key.arguments[2] as TypeKey<Any?>
            val p4Key = key.arguments[3] as TypeKey<Any?>
            val p5Key = key.arguments[4] as TypeKey<Any?>
            val p6Key = key.arguments[5] as TypeKey<Any?>
            val p7Key = key.arguments[6] as TypeKey<Any?>
            val p8Key = key.arguments[7] as TypeKey<Any?>
            val valueKey = key.arguments[8]
            return {
              { p1: Any?, p2: Any?, p3: Any?, p4: Any?, p5: Any?, p6: Any?, p7: Any?, p8: Any? ->
                buildChildInstance(valueKey) {
                  provide(p1Key) { p1 }
                  provide(p2Key) { p2 }
                  provide(p3Key) { p3 }
                  provide(p4Key) { p4 }
                  provide(p5Key) { p5 }
                  provide(p6Key) { p6 }
                  provide(p7Key) { p7 }
                  provide(p8Key) { p8 }
                }
              } as T
            }
          }
          "kotlin.Function9" -> {
            val p1Key = key.arguments[0] as TypeKey<Any?>
            val p2Key = key.arguments[1] as TypeKey<Any?>
            val p3Key = key.arguments[2] as TypeKey<Any?>
            val p4Key = key.arguments[3] as TypeKey<Any?>
            val p5Key = key.arguments[4] as TypeKey<Any?>
            val p6Key = key.arguments[5] as TypeKey<Any?>
            val p7Key = key.arguments[6] as TypeKey<Any?>
            val p8Key = key.arguments[7] as TypeKey<Any?>
            val p9Key = key.arguments[8] as TypeKey<Any?>
            val valueKey = key.arguments[9]
            return {
              { p1: Any?, p2: Any?, p3: Any?, p4: Any?, p5: Any?, p6: Any?, p7: Any?, p8: Any?, p9: Any? ->
                buildChildInstance(valueKey) {
                  provide(p1Key) { p1 }
                  provide(p2Key) { p2 }
                  provide(p3Key) { p3 }
                  provide(p4Key) { p4 }
                  provide(p5Key) { p5 }
                  provide(p6Key) { p6 }
                  provide(p7Key) { p7 }
                  provide(p8Key) { p8 }
                  provide(p9Key) { p9 }
                }
              } as T
            }
          }
          else -> throw IllegalArgumentException("Unsupported ")
        }
      }
    }

    return providers[key] as? ProviderScope.() -> T
      ?: parent?.getProvider(key, this)
      ?: throw NoProviderFoundException(key)
  }

  override fun <T> collectListProviders(
    key: TypeKey<T>,
    providers: MutableList<ProviderScope.() -> T>
  ) {
    listProviders[key]?.let { providers += it as List<ProviderScope.() -> T> }
    parent?.collectListProviders(key, providers)
  }
}

class DuplicatedProviderException(
  val key: TypeKey<*>,
  override val message: String? = "A provider for $key already exists"
) : IllegalStateException(message)

class NoProviderFoundException(
  val key: TypeKey<*>,
  override val message: String? = "No provider found for $key"
) : IllegalStateException(message)


inline fun <reified P1, reified T> ProviderScope.resolve(func: (P1) -> T) = func(get())

inline fun <reified P1, reified P2, reified T> ProviderScope.resolve(func: (P1, P2) -> T) =
  func(get(), get())

inline fun <reified P1, reified P2, reified P3, reified T> ProviderScope.resolve(func: (P1, P2, P3) -> T) =
  func(get(), get(), get())

inline fun <reified P1, reified P2, reified P3, reified P4, reified T> ProviderScope.resolve(func: (P1, P2, P3, P4) -> T) =
  func(get(), get(), get(), get())

inline fun <reified P1, reified P2, reified P3, reified P4, reified P5, reified T> ProviderScope.resolve(func: (P1, P2, P3, P4, P5) -> T) =
  func(get(), get(), get(), get(), get())

inline fun <reified P1, reified P2, reified P3, reified P4, reified P5, reified P6, reified T> ProviderScope.resolve(func: (P1, P2, P3, P4, P5, P6) -> T) =
  func(get(), get(), get(), get(), get(), get())

inline fun <reified P1, reified P2, reified P3, reified P4, reified P5, reified P6, reified P7, reified T> ProviderScope.resolve(func: (P1, P2, P3, P4, P5, P6, P7) -> T) =
  func(get(), get(), get(), get(), get(), get(), get())

inline fun <reified P1, reified P2, reified P3, reified P4, reified P5, reified P6, reified P7, reified P8, reified T> ProviderScope.resolve(func: (P1, P2, P3, P4, P5, P6, P7, P8) -> T) =
  func(get(), get(), get(), get(), get(), get(), get(), get())

inline fun <reified P1, reified P2, reified P3, reified P4, reified P5, reified P6, reified P7, reified P8, reified P9, reified T> ProviderScope.resolve(func: (P1, P2, P3, P4, P5, P6, P7, P8, P9) -> T) =
  func(get(), get(), get(), get(), get(), get(), get(), get(), get())

inline fun <reified P1, reified P2, reified P3, reified P4, reified P5, reified P6, reified P7, reified P8, reified P9, reified P10, reified T> ProviderScope.resolve(func: (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10) -> T) =
  func(get(), get(), get(), get(), get(), get(), get(), get(), get(), get())

inline fun <reified P1, reified P2, reified P3, reified P4, reified P5, reified P6, reified P7, reified P8, reified P9, reified P10, reified P11, reified T> ProviderScope.resolve(func: (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11) -> T) =
  func(get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get())

inline fun <reified P1, reified P2, reified P3, reified P4, reified P5, reified P6, reified P7, reified P8, reified P9, reified P10, reified P11, reified P12, reified T> ProviderScope.resolve(func: (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12) -> T) =
  func(get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get())

inline fun <reified P1, reified P2, reified P3, reified P4, reified P5, reified P6, reified P7, reified P8, reified P9, reified P10, reified P11, reified P12, reified P13, reified T> ProviderScope.resolve(func: (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13) -> T) =
  func(get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get())

inline fun <reified P1, reified P2, reified P3, reified P4, reified P5, reified P6, reified P7, reified P8, reified P9, reified P10, reified P11, reified P12, reified P13, reified P14, reified T> ProviderScope.resolve(func: (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14) -> T) =
  func(get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get())

inline fun <reified P1, reified P2, reified P3, reified P4, reified P5, reified P6, reified P7, reified P8, reified P9, reified P10, reified P11, reified P12, reified P13, reified P14, reified P15, reified T> ProviderScope.resolve(func: (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15) -> T) =
  func(get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get())

inline fun <reified P1, reified P2, reified P3, reified P4, reified P5, reified P6, reified P7, reified P8, reified P9, reified P10, reified P11, reified P12, reified P13, reified P14, reified P15, reified P16, reified T> ProviderScope.resolve(func: (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16) -> T) =
  func(get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get())

inline fun <reified P1, reified P2, reified P3, reified P4, reified P5, reified P6, reified P7, reified P8, reified P9, reified P10, reified P11, reified P12, reified P13, reified P14, reified P15, reified P16, reified P17, reified T> ProviderScope.resolve(func: (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17) -> T) =
  func(get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get())

inline fun <reified P1, reified P2, reified P3, reified P4, reified P5, reified P6, reified P7, reified P8, reified P9, reified P10, reified P11, reified P12, reified P13, reified P14, reified P15, reified P16, reified P17, reified P18, reified T> ProviderScope.resolve(func: (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18) -> T) =
  func(get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get())

inline fun <reified P1, reified P2, reified P3, reified P4, reified P5, reified P6, reified P7, reified P8, reified P9, reified P10, reified P11, reified P12, reified P13, reified P14, reified P15, reified P16, reified P17, reified P18, reified P19, reified T> ProviderScope.resolve(func: (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19) -> T) =
  func(get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get())

inline fun <reified P1, reified P2, reified P3, reified P4, reified P5, reified P6, reified P7, reified P8, reified P9, reified P10, reified P11, reified P12, reified P13, reified P14, reified P15, reified P16, reified P17, reified P18, reified P19, reified P20, reified T> ProviderScope.resolve(func: (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20) -> T) =
  func(get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get())

inline fun <reified P1, reified P2, reified P3, reified P4, reified P5, reified P6, reified P7, reified P8, reified P9, reified P10, reified P11, reified P12, reified P13, reified P14, reified P15, reified P16, reified P17, reified P18, reified P19, reified P20, reified P21, reified T> ProviderScope.resolve(func: (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21) -> T) =
  func(get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get())
