package com.ivianuu.essentials.app

import com.ivianuu.essentials.*
import com.ivianuu.essentials.logging.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.common.*
import com.ivianuu.injekt.scope.*

typealias ScopeInitializer<S> = () -> Unit

@Given
fun <@Given T : ScopeInitializer<S>, S : GivenScope> scopeInitializerElement(
    @Given instance: () -> T,
    @Given key: TypeKey<T>,
    @Given config: ScopeInitializerConfig<T> = ScopeInitializerConfig.DEFAULT
): ScopeInitializerElement<S> = ScopeInitializerElement(key, instance, config)

class ScopeInitializerConfig<out T : ScopeInitializer<*>>(
    val dependencies: Set<TypeKey<() -> Unit>> = emptySet(),
    val dependents: Set<TypeKey<() -> Unit>> = emptySet(),
) {
    companion object {
        val DEFAULT = ScopeInitializerConfig<Nothing>(emptySet(), emptySet())
    }
}

class ScopeInitializerElement<S>(
    val key: TypeKey<ScopeInitializer<S>>,
    val instance: () -> ScopeInitializer<S>,
    val config: ScopeInitializerConfig<ScopeInitializer<S>>
)

@Given
fun <S : GivenScope> scopeInitializerRunner(
    @Given initializers: Set<ScopeInitializerElement<S>> = emptySet(),
    @Given logger: Logger,
    @Given scopeKey: TypeKey<S>,
    @Given workerRunner: ScopeWorkerRunner<S>
): GivenScopeInitializer<S> = {
    initializers
        .sortedTopological(
            key = { it.key },
            dependencies = { it.config.dependencies },
            dependents = { it.config.dependents }
        )
        .forEach {
            logger.d { "$scopeKey initialize ${it.key}" }
            it.instance()()
        }
    workerRunner()
}
