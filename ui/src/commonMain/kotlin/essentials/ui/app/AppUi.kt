/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.ui.app

import androidx.compose.runtime.*
import androidx.compose.ui.util.*
import essentials.*
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
  decorators: List<@Composable (@Composable () -> Unit) -> AppUiDecoration<*>>,
  content: @Composable () -> Unit
): DecoratedAppUi {
  val combinedDecorator: @Composable (@Composable () -> Unit) -> Unit = remember(decorators) {
    decorators
      .fastFold({ it() }) { acc, decorator ->
        { content ->
          acc { decorator(content) }
        }
      }
  }

  combinedDecorator(content)
}
