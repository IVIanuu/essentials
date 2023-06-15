/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.resource

import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ivianuu.essentials.resource.Error
import com.ivianuu.essentials.resource.Idle
import com.ivianuu.essentials.resource.Loading
import com.ivianuu.essentials.resource.Resource
import com.ivianuu.essentials.resource.Success
import com.ivianuu.essentials.ui.animation.AnimatedContent
import com.ivianuu.essentials.ui.animation.ElementTransitionSpec
import com.ivianuu.essentials.ui.animation.crossFade
import com.ivianuu.essentials.ui.common.HorizontalList
import com.ivianuu.essentials.ui.common.VerticalList
import com.ivianuu.essentials.ui.layout.center

@Composable fun <T> ResourceVerticalListFor(
  resource: Resource<List<T>>,
  modifier: Modifier = Modifier,
  transitionSpec: ElementTransitionSpec<Resource<List<T>>> = ResourceBoxDefaults.transitionSpec,
  error: @Composable (Throwable) -> Unit = ResourceBoxDefaults.error,
  loading: @Composable () -> Unit = ResourceBoxDefaults.loading,
  idle: @Composable () -> Unit = {},
  successEmpty: @Composable () -> Unit = {},
  successItemContent: @Composable LazyItemScope.(T) -> Unit,
) {
  ResourceBox(
    resource = resource,
    modifier = modifier,
    transitionSpec = transitionSpec,
    error = error,
    loading = loading,
    idle = idle
  ) { items ->
    if (items.isNotEmpty()) {
      VerticalList { items(items) { successItemContent(it) } }
    } else {
      successEmpty()
    }
  }
}

@Composable fun <T> ResourceHorizontalListFor(
  resource: Resource<List<T>>,
  modifier: Modifier = Modifier,
  transitionSpec: ElementTransitionSpec<Resource<List<T>>> = ResourceBoxDefaults.transitionSpec,
  error: @Composable (Throwable) -> Unit = ResourceBoxDefaults.error,
  loading: @Composable () -> Unit = ResourceBoxDefaults.loading,
  idle: @Composable () -> Unit = {},
  successEmpty: @Composable () -> Unit = {},
  successItemContent: @Composable LazyItemScope.(T) -> Unit,
) {
  ResourceBox(
    resource = resource,
    modifier = modifier,
    transitionSpec = transitionSpec,
    error = error,
    loading = loading,
    idle = idle
  ) { items ->
    if (items.isNotEmpty()) {
      HorizontalList { items(items) { successItemContent(it) } }
    } else {
      successEmpty()
    }
  }
}

@Composable fun <T> ResourceBox(
  resource: Resource<T>,
  modifier: Modifier = Modifier,
  transitionSpec: ElementTransitionSpec<Resource<T>> = ResourceBoxDefaults.transitionSpec,
  error: @Composable (Throwable) -> Unit = ResourceBoxDefaults.error,
  loading: @Composable () -> Unit = ResourceBoxDefaults.loading,
  idle: @Composable () -> Unit = {},
  success: @Composable (T) -> Unit
) {
  AnimatedContent(
    state = resource,
    modifier = modifier,
    transitionSpec = transitionSpec,
    contentKey = { it::class }
  ) { itemToRender ->
    when (itemToRender) {
      is Idle -> idle()
      is Loading -> loading()
      is Success -> success(itemToRender.value)
      is Error -> error(itemToRender.error)
    }
  }
}

object ResourceBoxDefaults {
  val transitionSpec: ElementTransitionSpec<*> = { crossFade(tween(180)) }
  val error: @Composable (Throwable) -> Unit = {
    Text(
      modifier = Modifier
        .verticalScroll(rememberScrollState())
        .padding(16.dp),
      text = it.stackTraceToString(),
      color = MaterialTheme.colors.error,
      style = MaterialTheme.typography.body2
    )
  }
  val loading: @Composable () -> Unit = {
    CircularProgressIndicator(modifier = Modifier.center())
  }
}
