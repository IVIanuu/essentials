package com.ivianuu.essentials.ui.navigation

import androidx.compose.runtime.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.app.*
import com.ivianuu.essentials.logging.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.common.*

@Provide fun <@Spread T : KeyUiDecorator> keyUiDecoratorElement(
  instance: T,
  key: TypeKey<T>,
  loadingOrder: LoadingOrder<T> = LoadingOrder()
): KeyUiDecoratorElement =
  KeyUiDecoratorElement(key, instance as KeyUiDecorator, loadingOrder.cast())

typealias KeyUiDecorator = @Composable (@Composable () -> Unit) -> Unit

data class KeyUiDecoratorElement(
  val key: TypeKey<KeyUiDecorator>,
  val decorator: KeyUiDecorator,
  val loadingOrder: LoadingOrder<KeyUiDecorator>
) {
  companion object {
    @Provide val treeDescriptor = object : LoadingOrder.Descriptor<KeyUiDecoratorElement> {
      override fun key(item: KeyUiDecoratorElement) = item.key
      override fun loadingOrder(item: KeyUiDecoratorElement) = item.loadingOrder
    }
  }
}

typealias DecorateKeyUi = @Composable (@Composable () -> Unit) -> Unit

@Provide fun decorateKeyUi(
  elements: Set<KeyUiDecoratorElement> = emptySet(),
  logger: Logger
): DecorateKeyUi = { content ->
  remember {
    elements
      .sortedWithLoadingOrder()
      .reversed()
      .fold(content) { acc, element ->
        {
          d { "Decorate key ui ${element.key}" }
          element.decorator(acc)
        }
      }
  }.invoke()
}
