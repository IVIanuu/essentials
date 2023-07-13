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

fun interface ScreenDecorator : ExtensionPoint<ScreenDecorator> {
  @Composable operator fun invoke(content: @Composable () -> Unit)
}

fun interface DecorateScreen {
  @Composable operator fun invoke(content: @Composable () -> Unit)
}

context(Logger) @Provide fun decorateScreen(
  records: List<ExtensionPointRecord<ScreenDecorator>>
) = DecorateScreen { content ->
  val combinedDecorator: @Composable (@Composable () -> Unit) -> Unit = remember(records) {
    records
      .sortedWithLoadingOrder()
      .fold({ it() }) { acc, record ->
        { content ->
          acc {
            log { "decorate screen with ${record.key.value}" }
            record.instance(content)
          }
        }
      }
  }

  log { "decorate screen $content with combined $combinedDecorator" }

  combinedDecorator(content)
}
