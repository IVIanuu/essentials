/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.ui.app

import androidx.compose.runtime.*
import androidx.compose.ui.util.*
import essentials.*
import essentials.logging.*
import injekt.*
import kotlin.reflect.*

@Tag typealias AppUi = Unit

@Tag annotation class AppUiDecorationTag<K : Any>
typealias AppUiDecoration<K> = @AppUiDecorationTag<K> Unit

@Provide fun <@AddOn T : @AppUiDecorationTag<K> Unit, K : Any> appUiDecorationElement(
  key: KClass<K>,
  result: @Composable (@Composable () -> Unit) -> AppUiDecoration<K>,
  loadingOrder: LoadingOrder<K> = LoadingOrder()
) = LoadingOrderListElement<@Composable (@Composable () -> Unit) -> AppUiDecoration<*>>(key, result, loadingOrder)

@Tag typealias DecoratedAppUi = Unit

@Provide @Composable fun DecoratedAppUi(
  records: List<LoadingOrderListElement<@Composable (@Composable () -> Unit) -> AppUiDecoration<*>>>,
  logger: Logger,
  content: @Composable () -> Unit
): DecoratedAppUi {
  val combinedDecorator: @Composable (@Composable () -> Unit) -> Unit = remember(records) {
    records
      .sortedWithLoadingOrder()
      .fastFold({ it() }) { acc, record ->
        { content ->
          acc {
            logger.d { "decorate app ui ${record.key.qualifiedName}" }
            record.instance(content)
          }
        }
      }
  }

  logger.d { "decorate app ui $content with combined $combinedDecorator" }

  combinedDecorator(content)
}
