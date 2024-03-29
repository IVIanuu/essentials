/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.app

import androidx.compose.runtime.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.logging.*
import com.ivianuu.injekt.*

fun interface AppUi {
  @Composable fun Content()
}

fun interface AppUiDecorator : ExtensionPoint<AppUiDecorator> {
  @Composable fun DecoratedContent(content: @Composable () -> Unit)
}

@Provide class DecorateAppUi(
  private val records: List<ExtensionPointRecord<AppUiDecorator>>,
  private val logger: Logger
) {
  @Composable fun DecoratedContent(content: @Composable () -> Unit) {
    val combinedDecorator: @Composable (@Composable () -> Unit) -> Unit = remember(records) {
      records
        .sortedWithLoadingOrder()
        .fold({ it() }) { acc, record ->
          { content ->
            acc {
              logger.d { "decorate app ui ${record.key.qualifiedName}" }
              record.instance.DecoratedContent(content)
            }
          }
        }
    }

    logger.d { "decorate app ui $content with combined $combinedDecorator" }

    combinedDecorator(content)
  }
}
