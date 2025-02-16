/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.common

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.*
import com.ivianuu.essentials.resource.*
import com.ivianuu.essentials.ui.animation.*
import kotlin.reflect.*

@Composable fun <T> ResourceBox(
  resource: Resource<T>,
  modifier: Modifier = Modifier,
  contentAlignment: Alignment = Alignment.TopStart,
  transitionSpec: ElementTransitionSpec<ResourceBoxItem<T>> = ResourceBoxDefaults.transitionSpec(),
  error: @Composable (Throwable) -> Unit = ResourceBoxDefaults.error,
  loading: @Composable () -> Unit = ResourceBoxDefaults.loading,
  idle: @Composable () -> Unit = ResourceBoxDefaults.idle,
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
    contentAlignment = contentAlignment,
    transitionSpec = transitionSpec
  ) { itemToRender ->
    when (val value = itemToRender.value) {
      is Resource.Idle -> idle()
      is Resource.Loading -> loading()
      is Resource.Success -> success(value.value)
      is Resource.Error -> error(value.error)
    }
  }
}

@Stable class ResourceBoxItem<T>(
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
  fun <T> transitionSpec() = ElementTransitionSpec<T> { crossFade() }
  val error: @Composable (Throwable) -> Unit = {
    Text(
      modifier = Modifier
        .verticalScroll(rememberScrollState())
        .padding(16.dp),
      text = it.stackTraceToString(),
      color = MaterialTheme.colorScheme.error,
      style = MaterialTheme.typography.bodyMedium
    )
  }
  val loading: @Composable () -> Unit = {
    CircularProgressIndicator(modifier = Modifier.center())
  }
  val idle: @Composable () -> Unit = loading
}
