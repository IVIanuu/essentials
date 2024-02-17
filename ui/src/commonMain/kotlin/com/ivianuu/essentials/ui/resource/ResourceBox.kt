/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.resource

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.*
import com.ivianuu.essentials.resource.*
import com.ivianuu.essentials.ui.animation.*
import com.ivianuu.essentials.ui.common.*
import com.ivianuu.essentials.ui.layout.*
import kotlin.reflect.*

@Composable fun <T> ResourceVerticalListFor(
  resource: Resource<List<T>>,
  modifier: Modifier = Modifier,
  transitionSpec: ElementTransitionSpec<ResourceBoxItem<List<T>>> = ResourceBoxDefaults.transitionSpec,
  error: @Composable (Throwable) -> Unit = ResourceBoxDefaults.error,
  loading: @Composable () -> Unit = ResourceBoxDefaults.loading,
  successEmpty: @Composable () -> Unit = {},
  successItemContent: @Composable LazyItemScope.(T) -> Unit,
) {
  ResourceBox(
    resource = resource,
    modifier = modifier,
    transitionSpec = transitionSpec,
    error = error,
    loading = loading
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
  transitionSpec: ElementTransitionSpec<ResourceBoxItem<List<T>>> = ResourceBoxDefaults.transitionSpec,
  error: @Composable (Throwable) -> Unit = ResourceBoxDefaults.error,
  loading: @Composable () -> Unit = ResourceBoxDefaults.loading,
  successEmpty: @Composable () -> Unit = {},
  successItemContent: @Composable LazyItemScope.(T) -> Unit,
) {
  ResourceBox(
    resource = resource,
    modifier = modifier,
    transitionSpec = transitionSpec,
    error = error,
    loading = loading
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
  transitionSpec: ElementTransitionSpec<ResourceBoxItem<T>> = ResourceBoxDefaults.transitionSpec,
  error: @Composable (Throwable) -> Unit = ResourceBoxDefaults.error,
  loading: @Composable () -> Unit = ResourceBoxDefaults.loading,
  success: @Composable (T) -> Unit
) {
  // we only wanna animate if the resource type has changed
  val currentItem by remember(resource::class) {
    mutableStateOf(ResourceBoxItem(resource::class, resource))
  }

  currentItem.value = resource

  AnimatedContent(
    state = currentItem,
    modifier = modifier,
    transitionSpec = transitionSpec
  ) { itemToRender ->
    when (val value = itemToRender.value) {
      is Resource.Loading -> loading()
      is Resource.Success -> success(value.value)
      is Resource.Error -> error(value.error)
    }
  }
}

class ResourceBoxItem<T>(
  val clazz: KClass<out Resource<T>>,
  value: Resource<T>
) {
  var value by mutableStateOf(value)
    internal set

  override fun equals(other: Any?): Boolean = other is ResourceBoxItem<*> &&
      other.clazz == clazz

  override fun hashCode(): Int = clazz.hashCode()
}

object ResourceBoxDefaults {
  val transitionSpec: ElementTransitionSpec<*> = { crossFade() }
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
