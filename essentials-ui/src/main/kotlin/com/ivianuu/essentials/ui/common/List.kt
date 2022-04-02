/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.common

import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.ivianuu.essentials.app.LoadingOrder
import com.ivianuu.essentials.ui.UiDecorator
import com.ivianuu.essentials.ui.insets.localHorizontalInsetsPadding
import com.ivianuu.essentials.ui.insets.localVerticalInsetsPadding
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.Spread
import com.ivianuu.injekt.common.TypeKey

fun interface ListDecorator {
  operator fun ListDecoratorScope.invoke()
}

interface ListDecoratorScope : LazyListScope {
  val isVertical: Boolean

  fun content()
}

@Provide fun <@Spread T : ListDecorator> listDecoratorElement(
  instance: T,
  key: TypeKey<T>,
  loadingOrder: LoadingOrder<T> = LoadingOrder()
) = ListDecoratorElement(key, instance, loadingOrder as LoadingOrder<ListDecorator>)

data class ListDecoratorElement(
  val key: TypeKey<ListDecorator>,
  val decorator: ListDecorator,
  val loadingOrder: LoadingOrder<ListDecorator>
) {
  companion object {
    @Provide val treeDescriptor = object : LoadingOrder.Descriptor<ListDecoratorElement> {
      override fun key(item: ListDecoratorElement) = item.key
      override fun loadingOrder(item: ListDecoratorElement) = item.loadingOrder
    }

    @Provide val defaultElements: Collection<ListDecoratorElement> get() = emptyList()
  }
}

val LocalListDecorators = staticCompositionLocalOf<() -> List<ListDecoratorElement>> {
  { emptyList() }
}

fun interface ListDecoratorsProvider : UiDecorator

@Provide fun listDecoratorsProvider(
  decorators: () -> List<ListDecoratorElement>
) = ListDecoratorsProvider { content ->
  CompositionLocalProvider(
    LocalListDecorators provides decorators,
    content = content
  )
}

@Composable fun VerticalList(
  modifier: Modifier = Modifier,
  state: LazyListState = rememberLazyListState(),
  contentPadding: PaddingValues = localVerticalInsetsPadding(),
  reverseLayout: Boolean = false,
  verticalArrangement: Arrangement.Vertical =
    if (!reverseLayout) Arrangement.Top else Arrangement.Bottom,
  horizontalAlignment: Alignment.Horizontal = Alignment.Start,
  flingBehavior: FlingBehavior = ScrollableDefaults.flingBehavior(),
  decorate: Boolean = true,
  content: LazyListScope.() -> Unit
) {
  val decorators = if (decorate) remember(LocalListDecorators.current) else emptyList()
  LazyColumn(
    modifier = modifier,
    state = state,
    contentPadding = contentPadding,
    reverseLayout = reverseLayout,
    verticalArrangement = verticalArrangement,
    horizontalAlignment = horizontalAlignment,
    flingBehavior = flingBehavior
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
  decorate: Boolean = true,
  content: LazyListScope.() -> Unit
) {
  val decorators = if (decorate) remember(LocalListDecorators.current) else emptyList()
  LazyRow(
    modifier = modifier,
    state = state,
    contentPadding = contentPadding,
    reverseLayout = reverseLayout,
    horizontalArrangement = horizontalArrangement,
    verticalAlignment = verticalAlignment,
    flingBehavior = flingBehavior
  ) {
    decoratedContent(false, decorators, content)
  }
}

private fun LazyListScope.decoratedContent(
  isVertical: Boolean,
  decorators: List<ListDecoratorElement>,
  content: LazyListScope.() -> Unit
) {
  decorators
    .reversed()
    .fold(content) { acc, element ->
      decorator@ {
        with(element.decorator) {
          val scope =  object : ListDecoratorScope, LazyListScope by this@decorator {
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
