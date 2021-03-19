package com.ivianuu.essentials.app

import com.ivianuu.essentials.util.Logger
import com.ivianuu.essentials.util.d
import com.ivianuu.essentials.util.sortedTopological
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.common.ForTypeKey
import com.ivianuu.injekt.common.TypeKey
import com.ivianuu.injekt.common.typeKeyOf
import com.ivianuu.injekt.scope.GivenScope

typealias ScopeInitializer<S> = () -> Unit

// todo remove type parameter S once injekt is fixed
@Given
fun <@Given @ForTypeKey T : U, U : ScopeInitializer<S>, S : GivenScope> scopeInitializerElement(
    @Given instance: () -> T,
    @Given config: ScopeInitializerConfig<U> = ScopeInitializerConfig.DEFAULT
): ScopeInitializerElement<S> = ScopeInitializerElement(
    typeKeyOf<T>(), instance, config
)

data class ScopeInitializerConfig<out T : ScopeInitializer<*>>(
    val dependencies: Set<TypeKey<() -> Unit>> = emptySet(),
    val dependents: Set<TypeKey<() -> Unit>> = emptySet(),
) {
    companion object {
        val DEFAULT = ScopeInitializerConfig<Nothing>(emptySet(), emptySet())
    }
}

data class ScopeInitializerElement<S>(
    val key: TypeKey<*>,
    val instance: () -> ScopeInitializer<S>,
    val config: ScopeInitializerConfig<ScopeInitializer<S>>
)

@Given
fun <@ForTypeKey S : GivenScope> scopeInitializerRunner(
    @Given initializers: Set<ScopeInitializerElement<S>> = emptySet(),
    @Given logger: Logger,
): com.ivianuu.injekt.scope.GivenScopeInitializer<S> = {
    initializers
        .sortedTopological(
            key = { it.key },
            dependencies = { it.config.dependencies },
            dependents = { it.config.dependents }
        )
        .forEach {
            logger.d { "${typeKeyOf<S>()} initialize ${it.key}" }
            it.instance()()
        }
}
