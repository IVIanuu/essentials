/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.ivianuu.essentials.app.LoadingOrder
import com.ivianuu.essentials.app.Service
import com.ivianuu.essentials.app.ServiceElement
import com.ivianuu.essentials.app.sortedWithLoadingOrder
import com.ivianuu.essentials.logging.Logger
import com.ivianuu.essentials.logging.log
import com.ivianuu.essentials.ui.systembars.SystemBarManagerProvider
import com.ivianuu.injekt.Provide

fun interface UiDecorator : Service<UiDecorator> {
  @Composable operator fun invoke(p1: @Composable () -> Unit)
}

fun interface DecorateUi {
  @Composable operator fun invoke(p1: @Composable () -> Unit)
}

context(Logger) @Provide fun decorateUi(
  elements: List<ServiceElement<UiDecorator>>
) = DecorateUi { content ->
  val combinedDecorator: @Composable (@Composable () -> Unit) -> Unit = remember(elements) {
    elements
      .sortedWithLoadingOrder()
      .fold({ it() }) { acc, element ->
        { content ->
          acc {
            log { "Decorate ui ${element.key.value}" }
            element.instance(content)
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
