/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.common

import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.ivianuu.essentials.ExtensionPoint
import com.ivianuu.essentials.ExtensionPointRecord
import com.ivianuu.essentials.sortedWithLoadingOrder
import com.ivianuu.essentials.ui.AppUiDecorator
import com.ivianuu.essentials.ui.insets.localHorizontalInsetsPadding
import com.ivianuu.essentials.ui.insets.localVerticalInsetsPadding
import com.ivianuu.injekt.Provide

@Composable fun VerticalList(
  modifier: Modifier = Modifier,
  state: LazyListState = rememberLazyListState(),
  contentPadding: PaddingValues = localVerticalInsetsPadding(),
  reverseLayout: Boolean = false,
  verticalArrangement: Arrangement.Vertical =
    if (!reverseLayout) Arrangement.Top else Arrangement.Bottom,
  horizontalAlignment: Alignment.Horizontal = Alignment.Start,
  flingBehavior: FlingBehavior = ScrollableDefaults.flingBehavior(),
  userScrollEnabled: Boolean = true,
  decorate: Boolean = true,
  content: LazyListScope.() -> Unit
) {
  val decorators = if (decorate) remember(LocalListDecorators.current) else emptyList()
  LazyColumn(
    modifier = modifier.fillMaxHeight(),
    state = state,
    contentPadding = contentPadding,
    reverseLayout = reverseLayout,
    verticalArrangement = verticalArrangement,
    horizontalAlignment = horizontalAlignment,
    flingBehavior = flingBehavior,
    userScrollEnabled = userScrollEnabled
  ) {
    decoratedContent(true, decorators, content)
  }
}

@Composable fun HorizontalList(
  modifier: Modifier = Modifier,
  state: LazyListState = rememberLazyListState(),
  contentPadding: PaddingValues = localHorizontalInsetsPadding(),
  reverseLayout: Boolean = false,
  horizontalArrangement: Arrangement.Horizontal =
    if (!reverseLayout) Arrangement.Start else Arrangement.End,
  verticalAlignment: Alignment.Vertical = Alignment.Top,
  flingBehavior: FlingBehavior = ScrollableDefaults.flingBehavior(),
  userScrollEnabled: Boolean = true,
  decorate: Boolean = true,
  content: LazyListScope.() -> Unit
) {
  val decorators = if (decorate) remember(LocalListDecorators.current) else emptyList()
  LazyRow(
    modifier = modifier.fillMaxWidth(),
    state = state,
    contentPadding = contentPadding,
    reverseLayout = reverseLayout,
    horizontalArrangement = horizontalArrangement,
    verticalAlignment = verticalAlignment,
    flingBehavior = flingBehavior,
    userScrollEnabled = userScrollEnabled
  ) {
    decoratedContent(false, decorators, content)
  }
}

fun interface ListDecorator : ExtensionPoint<ListDecorator> {
  operator fun ListDecoratorScope.invoke()
}

@Stable interface ListDecoratorScope : LazyListScope {
  val isVertical: Boolean

  fun content()
}

val LocalListDecorators = staticCompositionLocalOf<() -> List<ExtensionPointRecord<ListDecorator>>> {
  { emptyList() }
}

fun interface ListDecoratorsProvider : AppUiDecorator

@Provide inline fun listDecoratorsProvider(
  crossinline decorators: () -> List<ExtensionPointRecord<ListDecorator>>
) = ListDecoratorsProvider { content ->
  CompositionLocalProvider(
    LocalListDecorators provides { decorators().sortedWithLoadingOrder() },
    content = content
  )
}

private fun LazyListScope.decoratedContent(
  isVertical: Boolean,
  decorators: List<ExtensionPointRecord<ListDecorator>>,
  content: LazyListScope.() -> Unit
) {
  decorators
    .fold(content) { acc, record ->
      decorator@{
        with(record.instance) {
          val scope = object : ListDecoratorScope, LazyListScope by this@decorator {
            override val isVertical: Boolean
              get() = isVertical

            override fun content() {
              acc()
            }
          }
          with(scope) {
            invoke()
          }
        }
      }
    }
    .invoke(this)
}
