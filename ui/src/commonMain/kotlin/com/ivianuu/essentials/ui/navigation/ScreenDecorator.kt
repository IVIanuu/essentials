/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.navigation

import androidx.compose.runtime.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.logging.*
import com.ivianuu.injekt.*

fun interface ScreenDecorator : ExtensionPoint<ScreenDecorator> {
  @Composable operator fun invoke(content: @Composable () -> Unit)
}

@Provide class DecorateScreen(
  private val records: List<ExtensionPointRecord<ScreenDecorator>>,
  private val logger: Logger
) {
  @Composable operator fun invoke(content: @Composable () -> Unit) {
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
}
