/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.ui.app

import androidx.compose.runtime.*
import androidx.compose.ui.util.*
import essentials.*
import essentials.logging.*
import injekt.*

@Tag typealias AppUi = Unit

@Stable fun interface AppUiDecorator : ExtensionPoint<AppUiDecorator> {
  @Composable fun DecoratedContent(content: @Composable () -> Unit)
}

@Tag typealias DecoratedAppUi = Unit

@Provide @Composable fun DecoratedAppUi(
  records: List<ExtensionPointRecord<AppUiDecorator>>,
  scope: Scope<*> = inject,
  content: @Composable () -> Unit
): DecoratedAppUi {
  val combinedDecorator: @Composable (@Composable () -> Unit) -> Unit = remember(records) {
    records
      .sortedWithLoadingOrder()
      .fastFold({ it() }) { acc, record ->
        { content ->
          acc {
            d { "decorate app ui ${record.key.qualifiedName}" }
            record.instance.DecoratedContent(content)
          }
        }
      }
  }

  d { "decorate app ui $content with combined $combinedDecorator" }

  combinedDecorator(content)
}
