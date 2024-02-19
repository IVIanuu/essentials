/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.app

import androidx.compose.runtime.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.logging.*
import com.ivianuu.essentials.ui.systembars.*
import com.ivianuu.injekt.*

fun interface AppUiDecorator : ExtensionPoint<AppUiDecorator> {
  @Composable operator fun invoke(content: @Composable () -> Unit)
}

@Provide class DecorateAppUi(
  private val records: List<ExtensionPointRecord<AppUiDecorator>>,
  private val logger: Logger
) {
  @Composable operator fun invoke(content: @Composable () -> Unit) {
    val combinedDecorator: @Composable (@Composable () -> Unit) -> Unit = remember(records) {
      records
        .sortedWithLoadingOrder()
        .fold({ it() }) { acc, record ->
          { content ->
            acc {
              logger.log { "decorate app ui ${record.key.value}" }
              record.instance(content)
            }
          }
        }
    }

    logger.log { "decorate app ui $content with combined $combinedDecorator" }

    combinedDecorator(content)
  }
}

fun interface AppThemeDecorator : AppUiDecorator {
  @Provide companion object {
    @Provide val loadingOrder
      get() = LoadingOrder<AppThemeDecorator>()
        .after<SystemBarManagerProvider>()
  }
}
