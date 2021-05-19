package com.ivianuu.essentials.ui.navigation

import androidx.compose.runtime.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.logging.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.common.*

@Provide fun <@Spread T : KeyUiDecorator> keyUiDecoratorElement(
  instance: T,
  key: TypeKey<T>,
  config: KeyUiDecoratorConfig<T> = KeyUiDecoratorConfig.DEFAULT
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

@Provide object KeyUiDecoratorElementTreeDescriptor : TreeDescriptor<KeyUiDecoratorElement> {
  override fun key(value: KeyUiDecoratorElement): Any = value.key
  override fun dependencies(value: KeyUiDecoratorElement): Set<Any> = value.config.dependencies
  override fun dependents(value: KeyUiDecoratorElement): Set<Any> = value.config.dependents
}

typealias DecorateKeyUi = @Composable (@Composable () -> Unit) -> Unit

@Provide fun decorateKeyUi(
  elements: Set<KeyUiDecoratorElement> = emptySet(),
  _: Logger
): DecorateKeyUi = { content ->
  remember {
    elements
      .sortedTopological()
      .reversed()
      .fold(content) { acc, element ->
        {
          d { "Decorate key ui ${element.key}" }
          element.decorator(acc)
        }
      }
  }.invoke()
}
