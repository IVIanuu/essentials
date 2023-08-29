/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.ivianuu.essentials.ExtensionPoint
import com.ivianuu.essentials.ExtensionPointRecord
import com.ivianuu.essentials.logging.Logger
import com.ivianuu.essentials.logging.log
import com.ivianuu.essentials.sortedWithLoadingOrder
import com.ivianuu.injekt.Provide

fun interface ScreenDecorator : ExtensionPoint<ScreenDecorator> {
  @Composable operator fun invoke(content: @Composable () -> Unit)
}

fun interface DecorateScreen {
  @Composable operator fun invoke(content: @Composable () -> Unit)
}

@Provide fun decorateScreen(
  records: List<ExtensionPointRecord<ScreenDecorator>>,
  logger: Logger
) = DecorateScreen { content ->
  val combinedDecorator: @Composable (@Composable () -> Unit) -> Unit = remember(records) {
    records
      .sortedWithLoadingOrder()
      .fold({ it() }) { acc, record ->
        { content ->
          acc {
            logger.log { "decorate screen with ${record.key.value}" }
            record.instance(content)
          }
        }
      }
  }

  logger.log { "decorate screen $content with combined $combinedDecorator" }

  combinedDecorator(content)
}