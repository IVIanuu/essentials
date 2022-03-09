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

inline fun <reified T> ProviderScope.getOrNull(): T? = getOrNull(typeKeyOf())

fun <T> ProviderScope.getOrNull(key: TypeKey<T>): T? = getSingleProviderOrNull(key)?.invoke(this)

inline fun <reified T> ProviderScope.get(): T = get(typeKeyOf())

fun <T> ProviderScope.get(key: TypeKey<T>): T = getOrNull(key)
  ?: throw NoProviderFoundException(key)

inline fun <reified T> ProviderRegistry.provide(crossinline provider: ProviderScope.() -> T) =
  provide(typeKeyOf(), provider)

inline fun <T> ProviderRegistry.provide(key: TypeKey<T>, crossinline provider: ProviderScope.() -> T) =
  provide(key, Provider(false, provider))

inline fun <reified T> ProviderRegistry.provideDefault(crossinline provider: ProviderScope.() -> T) =
  provideDefault(typeKeyOf(), provider)

inline fun <T> ProviderRegistry.provideDefault(key: TypeKey<T>, crossinline provider: ProviderScope.() -> T) =
  provide(key, Provider(true, provider))

inline fun ProviderRegistry.provideGeneric(crossinline providerFactory: (TypeKey<*>) -> Provider<*>?) =
  provideGeneric(GenericProviderFactory(false, providerFactory))

inline fun ProviderRegistry.provideDefaultGeneric(crossinline providerFactory: (TypeKey<*>) -> Provider<*>?) =
  provideGeneric(GenericProviderFactory(true, providerFactory))

interface ProviderScope {
  fun <T> getSingleProviderOrNull(key: TypeKey<T>): Provider<T>?

  fun <T> getGenericProviderOrNull(key: TypeKey<T>): Provider<T>?

  fun <T> collectAllProviders(key: TypeKey<T>, providers: MutableList<ProviderScope.() -> T>)
}

interface ProviderRegistry {
  fun <T> provide(key: TypeKey<T>, provider: Provider<T>)

  fun provideGeneric(providerFactory: GenericProviderFactory)
}

interface Provider<out T> : (ProviderScope) -> T {
  val default: Boolean

  companion object {
    inline operator fun <T> invoke(default: Boolean = false, crossinline provider: ProviderScope.() -> T) =
      object : Provider<T> {
        override val default: Boolean
          get() = default

        override fun invoke(p1: ProviderScope): T = provider(p1)
      }
  }
}

interface GenericProviderFactory : (TypeKey<*>) -> Provider<Any?>? {
  val default: Boolean

  companion object {
    inline operator fun invoke(default: Boolean = false, crossinline providerFactory: (TypeKey<*>) -> Provider<*>?) =
      object : GenericProviderFactory {
        override val default: Boolean
          get() = default

        override fun invoke(p1: TypeKey<*>): Provider<Any?>? = providerFactory(p1)
      }
  }
}

@PublishedApi internal class ProviderScopeImpl(
  private val parent: ProviderScope?
) : ProviderScope, ProviderRegistry {
  private val providers = mutableMapOf<TypeKey<*>, MutableList<Provider<Any?>>>()
  private val genericProviderFactories = mutableListOf<GenericProviderFactory>()

  init {
    frameworkProviders()
  }
  
  override fun <T> provide(key: TypeKey<T>, provider: Provider<T>) {
    providers.getOrPut(key) { mutableListOf() } += provider
  }

  override fun provideGeneric(providerFactory: GenericProviderFactory) {
    genericProviderFactories += providerFactory
  }

  override fun <T> getSingleProviderOrNull(key: TypeKey<T>): Provider<T>? {
    val providersForKey = providers[key] 
      ?: return parent?.getSingleProviderOrNull(key)
      ?: return getGenericProviderOrNull(key)
      ?: throw NoProviderFoundException(key)
    
    if (providersForKey.size > 1) 
      throw MultipleProvidersException(key)

    return providersForKey.firstOrNull() as? Provider<T>
  }

  override fun <T> getGenericProviderOrNull(key: TypeKey<T>): Provider<T>? {
    var resultProvider: (ProviderScope.() -> Any?)? = null
    for (providerFactory in genericProviderFactories) {
      val provider = providerFactory(key)
      if (provider != null) {
        if (resultProvider != null)
          throw MultipleProvidersException(key)
        else resultProvider = provider
      }
    }

    return resultProvider as? Provider<T>
  }

  override fun <T> collectAllProviders(
    key: TypeKey<T>,
    providers: MutableList<ProviderScope.() -> T>
  ) {
    parent?.collectAllProviders(key, providers)
    for (genericProviderFactory in genericProviderFactories)
      genericProviderFactory(key)?.let { providers += it as ProviderScope.() -> T }
    this.providers[key]?.let { providers += it as List<ProviderScope.() -> T> }
  }
}

class MultipleProvidersException(
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

fun ProviderRegistry.frameworkProviders() {
  provideGeneric { key ->
    if (key.classifierFqName != "kotlin.collections.List") null
    else {
      val elementKey = key.arguments[0]
      Provider {
        val providers = mutableListOf<ProviderScope.() -> Any?>()
        collectAllProviders(elementKey as TypeKey<Any?>, providers)
        if (providers.isEmpty())
          throw NoProviderFoundException(key, "No providers for found element $elementKey")
        providers
          .map { it(this) }
      }
    }
  }
  
  provideGeneric { key ->
    if (!key.classifierFqName.startsWith("kotlin.Function")) null
    else when (key.classifierFqName) {
      "kotlin.Function0" -> {
        val valueKey = key.arguments[0]
        Provider {
          val func: () -> Any? = {
            get(valueKey)
          }
          func
        }
      }
      "kotlin.Function1" -> {
        val p1Key = key.arguments[0] as TypeKey<Any?>
        val valueKey = key.arguments[1]
        Provider {
          { p1: Any? ->
            buildChildInstance(valueKey) {
              provide(p1Key) { p1 }
            }
          }
        }
      }
      "kotlin.Function2" -> {
        val p1Key = key.arguments[0] as TypeKey<Any?>
        val p2Key = key.arguments[1] as TypeKey<Any?>
        val valueKey = key.arguments[2]
        Provider {
          { p1: Any?, p2: Any? ->
            buildChildInstance(valueKey) {
              provide(p1Key) { p1 }
              provide(p2Key) { p2 }
            }
          }
        }
      }
      "kotlin.Function3" -> {
        val p1Key = key.arguments[0] as TypeKey<Any?>
        val p2Key = key.arguments[1] as TypeKey<Any?>
        val p3Key = key.arguments[2] as TypeKey<Any?>
        val valueKey = key.arguments[3]
        Provider {
          { p1: Any?, p2: Any?, p3: Any? ->
            buildChildInstance(valueKey) {
              provide(p1Key) { p1 }
              provide(p2Key) { p2 }
              provide(p3Key) { p3 }
            }
          }
        }
      }
      "kotlin.Function4" -> {
        val p1Key = key.arguments[0] as TypeKey<Any?>
        val p2Key = key.arguments[1] as TypeKey<Any?>
        val p3Key = key.arguments[2] as TypeKey<Any?>
        val p4Key = key.arguments[3] as TypeKey<Any?>
        val valueKey = key.arguments[4]
        Provider {
          { p1: Any?, p2: Any?, p3: Any?, p4: Any? ->
            buildChildInstance(valueKey) {
              provide(p1Key) { p1 }
              provide(p2Key) { p2 }
              provide(p3Key) { p3 }
              provide(p4Key) { p4 }
            }
          }
        }
      }
      "kotlin.Function5" -> {
        val p1Key = key.arguments[0] as TypeKey<Any?>
        val p2Key = key.arguments[1] as TypeKey<Any?>
        val p3Key = key.arguments[2] as TypeKey<Any?>
        val p4Key = key.arguments[3] as TypeKey<Any?>
        val p5Key = key.arguments[4] as TypeKey<Any?>
        val valueKey = key.arguments[5]
        Provider {
          { p1: Any?, p2: Any?, p3: Any?, p4: Any?, p5: Any? ->
            buildChildInstance(valueKey) {
              provide(p1Key) { p1 }
              provide(p2Key) { p2 }
              provide(p3Key) { p3 }
              provide(p4Key) { p4 }
              provide(p5Key) { p5 }
            }
          }
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
        Provider {
          { p1: Any?, p2: Any?, p3: Any?, p4: Any?, p5: Any?, p6: Any? ->
            buildChildInstance(valueKey) {
              provide(p1Key) { p1 }
              provide(p2Key) { p2 }
              provide(p3Key) { p3 }
              provide(p4Key) { p4 }
              provide(p5Key) { p5 }
              provide(p6Key) { p6 }
            }
          }
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
        Provider {
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
          }
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
        Provider {
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
          }
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
        Provider {
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
          }
        }
      }
      else -> throw IllegalArgumentException("Unsupported ")
    }
  }
}
