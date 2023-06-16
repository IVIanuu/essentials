/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.ivianuu.essentials.app.ExtensionPoint
import com.ivianuu.essentials.app.ExtensionPointRecord
import com.ivianuu.essentials.app.sortedWithLoadingOrder
import com.ivianuu.essentials.logging.Logger
import com.ivianuu.essentials.logging.log
import com.ivianuu.injekt.Provide

fun interface ScreenDecorator<S : Screen<*>> : ExtensionPoint<ScreenDecorator<S>> {
  @Composable operator fun invoke(content: @Composable () -> Unit)
}

fun interface DecorateScreen<out S : Screen<*>> {
  @Composable operator fun invoke(content: @Composable () -> Unit)
}

@Provide fun <S : Screen<*>> decorateScreen(
  records: List<ExtensionPointRecord<ScreenDecorator<in S>>>,
  logger: Logger
) = DecorateScreen<S> { content ->
  val combinedDecorator: @Composable (@Composable () -> Unit) -> Unit = remember(records) {
    records
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
