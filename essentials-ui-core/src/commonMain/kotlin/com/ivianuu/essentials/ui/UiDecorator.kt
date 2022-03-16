/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui

import androidx.compose.runtime.*
import com.ivianuu.essentials.app.*
import com.ivianuu.essentials.logging.*
import com.ivianuu.essentials.ui.systembars.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.common.*

@Provide fun <@Spread T : UiDecorator> uiDecoratorElement(
  instance: T,
  key: TypeKey<T>,
  loadingOrder: LoadingOrder<T> = LoadingOrder()
) = UiDecoratorElement(key, instance, loadingOrder as LoadingOrder<UiDecorator>)

fun interface UiDecorator {
  @Composable operator fun invoke(p1: @Composable () -> Unit)
}

data class UiDecoratorElement(
  val key: TypeKey<UiDecorator>,
  val decorator: UiDecorator,
  val loadingOrder: LoadingOrder<UiDecorator>
) {
  companion object {
    @Provide val treeDescriptor = object : LoadingOrder.Descriptor<UiDecoratorElement> {
      override fun key(item: UiDecoratorElement) = item.key
      override fun loadingOrder(item: UiDecoratorElement) = item.loadingOrder
    }

    @Provide val defaultElements get() = emptyList<UiDecoratorElement>()
  }
}

fun interface DecorateUi {
  @Composable operator fun invoke(p1: @Composable () -> Unit)
}

@Provide fun decorateUi(
  elements: List<UiDecoratorElement>,
  L: Logger
) = DecorateUi { content ->
  val combinedDecorator: @Composable (@Composable () -> Unit) -> Unit = remember(elements) {
    elements
      .sortedWithLoadingOrder()
      .fold({ it() }) { acc, element ->
        { content ->
          acc {
            log { "Decorate ui ${element.key.value}" }
            element.decorator(content)
          }
        }
      }
  }

  log { "decorate with combined $combinedDecorator" }

  combinedDecorator(content)
}

fun interface AppTheme : UiDecorator

@Provide val appThemeConfig = LoadingOrder<AppTheme>()
  .after<SystemBarManagerProvider>()
