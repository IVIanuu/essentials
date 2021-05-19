package com.ivianuu.essentials.app

import com.ivianuu.essentials.*
import com.ivianuu.essentials.logging.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.common.*
import com.ivianuu.injekt.scope.*

typealias ScopeInitializer<S> = () -> Unit

@Provide fun <@Spread T : ScopeInitializer<S>, S : Scope> scopeInitializerElement(
  instance: () -> T,
  key: TypeKey<T>,
  config: ScopeInitializerConfig<T> = ScopeInitializerConfig.DEFAULT
): ScopeInitializerElement<S> = ScopeInitializerElement(key, instance, config)

class ScopeInitializerConfig<out T : ScopeInitializer<*>>(
  val dependencies: Set<TypeKey<() -> Unit>> = emptySet(),
  val dependents: Set<TypeKey<() -> Unit>> = emptySet(),
) {
  companion object {
    val DEFAULT = ScopeInitializerConfig<Nothing>(emptySet(), emptySet())
  }
}

data class ScopeInitializerElement<S>(
  val key: TypeKey<ScopeInitializer<S>>,
  val instance: () -> ScopeInitializer<S>,
  val config: ScopeInitializerConfig<ScopeInitializer<S>>
) {
  companion object {
    @Provide val treeDescriptor = object : TreeDescriptor<ScopeInitializerElement<*>> {
      override fun key(value: ScopeInitializerElement<*>): TypeKey<*> = value.key
      override fun dependents(value: ScopeInitializerElement<*>): Set<TypeKey<*>> =
        value.config.dependents

      override fun dependencies(value: ScopeInitializerElement<*>): Set<TypeKey<*>> =
        value.config.dependencies
    }
  }
}

@Provide fun <S : Scope> scopeInitializerRunner(
  initializers: Set<ScopeInitializerElement<S>> = emptySet(),
  scopeKey: TypeKey<S>,
  workerRunner: ScopeWorkerRunner<S>,
  _: Logger
): com.ivianuu.injekt.scope.ScopeInitializer<S> = {
  initializers
    .sortedTopological()
    .forEach {
      d { "$scopeKey initialize ${it.key}" }
      it.instance()()
    }
  workerRunner()
}
