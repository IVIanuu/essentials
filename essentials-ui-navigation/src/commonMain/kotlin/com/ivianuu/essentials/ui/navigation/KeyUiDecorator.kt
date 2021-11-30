/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.ivianuu.essentials.app.LoadingOrder
import com.ivianuu.essentials.app.sortedWithLoadingOrder
import com.ivianuu.essentials.cast
import com.ivianuu.essentials.logging.Logger
import com.ivianuu.essentials.logging.log
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.Spread
import com.ivianuu.injekt.common.TypeKey

@Provide fun <@Spread T : KeyUiDecorator> keyUiDecoratorElement(
  instance: T,
  key: TypeKey<T>,
  loadingOrder: LoadingOrder<T> = LoadingOrder()
) = KeyUiDecoratorElement(key, instance, loadingOrder.cast())

fun interface KeyUiDecorator : @Composable (@Composable () -> Unit) -> Unit

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

    @Provide val defaultElements: Collection<KeyUiDecoratorElement> get() = emptyList()
  }
}

fun interface DecorateKeyUi : @Composable (@Composable () -> Unit) -> Unit

@Provide fun decorateKeyUi(
  elements: List<KeyUiDecoratorElement>,
  L: Logger
) = DecorateKeyUi { content ->
  remember {
    elements
      .sortedWithLoadingOrder()
      .reversed()
      .fold(content) { acc, element ->
        {
          log { "Decorate key ui ${element.key.value}" }
          element.decorator(acc)
        }
      }
  }()
}
