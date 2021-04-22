package com.ivianuu.essentials.ui.navigation

import androidx.compose.runtime.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.logging.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.common.*
import com.ivianuu.injekt.scope.*

@Given
fun <@Given T : KeyUiDecorator> keyUiDecoratorElement(
    @Given instance: T,
    @Given key: TypeKey<T>,
    @Given config: KeyUiDecoratorConfig<T> = KeyUiDecoratorConfig.DEFAULT
): KeyUiDecoratorElement = KeyUiDecoratorElement(key, instance as KeyUiDecorator, config)

class KeyUiDecoratorConfig<out T : KeyUiDecorator>(
    val dependencies: Set<TypeKey<KeyUiDecorator>> = emptySet(),
    val dependents: Set<TypeKey<KeyUiDecorator>> = emptySet(),
) {
    companion object {
        val DEFAULT = KeyUiDecoratorConfig<Nothing>(emptySet(), emptySet())
    }
}

typealias KeyUiDecorator = @Composable (@Composable () -> Unit) -> Unit

data class KeyUiDecoratorElement(
    val key: TypeKey<KeyUiDecorator>,
    val decorator: KeyUiDecorator,
    val config: KeyUiDecoratorConfig<*>
)

typealias DecorateKeyUi = @Composable (@Composable () -> Unit) -> Unit

@Given
fun decorateKeyUi(
    @Given elements: Set<KeyUiDecoratorElement> = emptySet(),
    @Given logger: Logger
): DecorateKeyUi = { content ->
    remember {
        elements
            .sortedTopological(
                key = { it.key },
                dependencies = { it.config.dependencies },
                dependents = { it.config.dependents }
            )
            .reversed()
            .fold(content) { acc, element ->
                {
                    logger.d { "Decorate key ui ${element.key}" }
                    element.decorator(acc)
                }
            }
    }.invoke()
}