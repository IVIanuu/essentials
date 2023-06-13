/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.ivianuu.essentials.app.Service
import com.ivianuu.essentials.app.ServiceElement
import com.ivianuu.essentials.app.sortedWithLoadingOrder
import com.ivianuu.essentials.logging.Logger
import com.ivianuu.essentials.logging.log
import com.ivianuu.injekt.Provide

fun interface ScreenDecorator : Service<ScreenDecorator> {
  @Composable operator fun invoke(content: @Composable () -> Unit)
}

fun interface DecorateScreen {
  @Composable operator fun invoke(content: @Composable () -> Unit)
}

@Provide fun decorateScreen(
  elements: List<ServiceElement<ScreenDecorator>>,
  logger: Logger
) = DecorateScreen { content ->
  val combinedDecorator: @Composable (@Composable () -> Unit) -> Unit = remember(elements) {
    elements
      .sortedWithLoadingOrder()
      .fold({ it() }) { acc, element ->
        { content ->
          acc {
            logger.log { "Decorate screen ${element.key.value}" }
            element.instance(content)
          }
        }
      }
  }

  combinedDecorator(content)
}
