/*
 * Copyright 2021 Manuel Wrage
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
import com.ivianuu.injekt.Tag
import com.ivianuu.injekt.common.TypeKey

@Provide fun <@Spread T : KeyUiDecorator<K>, K> keyUiDecoratorElement(
  instance: T,
  key: TypeKey<T>,
  loadingOrder: LoadingOrder<T> = LoadingOrder()
): KeyUiDecoratorElement =
  KeyUiDecoratorElement(key, instance as KeyUiDecorator<*>, loadingOrder.cast())

@Tag annotation class KeyUiDecoratorTag<K>
typealias KeyUiDecorator<K> = @KeyUiDecoratorTag<K> @Composable (@Composable () -> Unit) -> Unit

data class KeyUiDecoratorElement(
  val key: TypeKey<KeyUiDecorator<*>>,
  val decorator: KeyUiDecorator<*>,
  val loadingOrder: LoadingOrder<KeyUiDecorator<*>>
) {
  companion object {
    @Provide val treeDescriptor = object : LoadingOrder.Descriptor<KeyUiDecoratorElement> {
      override fun key(item: KeyUiDecoratorElement) = item.key
      override fun loadingOrder(item: KeyUiDecoratorElement) = item.loadingOrder
    }
  }
}

@Tag annotation class DecorateKeyUiTag
typealias DecorateKeyUi = @DecorateKeyUiTag @Composable (@Composable () -> Unit) -> Unit

@Provide fun decorateKeyUi(
  elements: List<KeyUiDecoratorElement> = emptyList(),
  logger: Logger
): DecorateKeyUi = { content ->
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
  }.invoke()
}
