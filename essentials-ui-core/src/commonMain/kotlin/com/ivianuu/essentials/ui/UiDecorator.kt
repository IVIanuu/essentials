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

package com.ivianuu.essentials.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.ivianuu.essentials.app.LoadingOrder
import com.ivianuu.essentials.app.sortedWithLoadingOrder
import com.ivianuu.essentials.cast
import com.ivianuu.essentials.logging.Logger
import com.ivianuu.essentials.logging.log
import com.ivianuu.essentials.ui.systembars.SystemBarManagerProvider
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.Spread
import com.ivianuu.injekt.Tag
import com.ivianuu.injekt.common.TypeKey

@Provide fun <@Spread T : UiDecorator<K>, K> uiDecoratorElement(
  instance: T,
  key: TypeKey<T>,
  loadingOrder: LoadingOrder<T> = LoadingOrder()
): UiDecoratorElement = UiDecoratorElement(key, instance as UiDecorator<*>, loadingOrder.cast())

@Tag annotation class UiDecoratorTag<K>
typealias UiDecorator<K> = @UiDecoratorTag<K> @Composable (@Composable () -> Unit) -> Unit

data class UiDecoratorElement(
  val key: TypeKey<UiDecorator<*>>,
  val decorator: UiDecorator<*>,
  val loadingOrder: LoadingOrder<UiDecorator<*>>
) {
  companion object {
    @Provide val treeDescriptor = object : LoadingOrder.Descriptor<UiDecoratorElement> {
      override fun key(item: UiDecoratorElement) = item.key
      override fun loadingOrder(item: UiDecoratorElement) = item.loadingOrder
    }

    @Provide fun defaultElements() = emptyList<UiDecoratorElement>()
  }
}

@Tag annotation class DecorateUiTag
typealias DecorateUi = @DecorateUiTag @Composable (@Composable () -> Unit) -> Unit

@Provide fun decorateUi(
  elements: List<UiDecoratorElement>,
  L: Logger
): DecorateUi = { content ->
  remember {
    elements
      .sortedWithLoadingOrder()
      .reversed()
      .fold(content) { acc, element ->
        {
          log { "Decorate ui ${element.key.value}" }
          element.decorator(acc)
        }
      }
  }.invoke()
}

object AppTheme

@Provide val appThemeConfig = LoadingOrder<UiDecorator<AppTheme>>()
  .after<UiDecorator<SystemBarManagerProvider>>()
