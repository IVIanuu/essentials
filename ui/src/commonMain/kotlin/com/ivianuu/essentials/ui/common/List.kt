/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.common

import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.platform.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.ui.*
import com.ivianuu.injekt.*

@Composable fun VerticalList(
  modifier: Modifier = Modifier,
  state: LazyListState = rememberLazyListState(),
  contentPadding: PaddingValues = WindowInsets.safeContent.asPaddingValues(),
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
    reverseLayout = reverseLayout,
    verticalArrangement = verticalArrangement,
    horizontalAlignment = horizontalAlignment,
    flingBehavior = flingBehavior,
    userScrollEnabled = userScrollEnabled
  ) {
    decoratedContent(true, decorators) {
      item {
        Spacer(
          Modifier.windowInsetsTopHeight(WindowInsets(top = contentPadding.calculateTopPadding()))
        )
      }

      content()

      item {
        Spacer(
          Modifier.windowInsetsBottomHeight(WindowInsets(bottom = contentPadding.calculateBottomPadding()))
        )
      }
    }
  }
}

@Composable fun HorizontalList(
  modifier: Modifier = Modifier,
  state: LazyListState = rememberLazyListState(),
  contentPadding: PaddingValues = WindowInsets.safeContent.asPaddingValues(),
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
  val layoutDirection = LocalLayoutDirection.current
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
    decoratedContent(false, decorators) {
      item {
        Spacer(
          Modifier.windowInsetsStartWidth(WindowInsets(left = contentPadding.calculateStartPadding(layoutDirection)))
        )
      }

      content()

      item {
        Spacer(
          Modifier.windowInsetsBottomHeight(WindowInsets(bottom = contentPadding.calculateEndPadding(layoutDirection)))
        )
      }
    }
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
