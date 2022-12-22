/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.resource

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ivianuu.essentials.resource.Error
import com.ivianuu.essentials.resource.Idle
import com.ivianuu.essentials.resource.Loading
import com.ivianuu.essentials.resource.Resource
import com.ivianuu.essentials.resource.Success
import com.ivianuu.essentials.time.milliseconds
import com.ivianuu.essentials.ui.animation.AnimatedBox
import com.ivianuu.essentials.ui.animation.transition.CrossFadeStackTransition
import com.ivianuu.essentials.ui.animation.transition.StackTransition
import com.ivianuu.essentials.ui.animation.transition.defaultAnimationSpec
import com.ivianuu.essentials.ui.common.HorizontalList
import com.ivianuu.essentials.ui.common.VerticalList
import com.ivianuu.essentials.ui.layout.center
import kotlin.reflect.KClass

@Composable fun <T> ResourceVerticalListFor(
  resource: Resource<List<T>>,
  modifier: Modifier = Modifier,
  transition: StackTransition = ResourceBoxDefaults.transition,
  error: @Composable (Throwable) -> Unit = ResourceBoxDefaults.error,
  loading: @Composable () -> Unit = ResourceBoxDefaults.loading,
  idle: @Composable () -> Unit = {},
  successEmpty: @Composable () -> Unit = {},
  successItemContent: @Composable context(LazyItemScope) (T) -> Unit,
) {
  ResourceBox(
    resource = resource,
    modifier = modifier,
    transition = transition,
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
  transition: StackTransition = ResourceBoxDefaults.transition,
  error: @Composable (Throwable) -> Unit = ResourceBoxDefaults.error,
  loading: @Composable () -> Unit = ResourceBoxDefaults.loading,
  idle: @Composable () -> Unit = {},
  successEmpty: @Composable () -> Unit = {},
  successItemContent: @Composable context(LazyItemScope) (T) -> Unit,
) {
  ResourceBox(
    resource = resource,
    modifier = modifier,
    transition = transition,
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
  transition: StackTransition = ResourceBoxDefaults.transition,
  error: @Composable (Throwable) -> Unit = ResourceBoxDefaults.error,
  loading: @Composable () -> Unit = ResourceBoxDefaults.loading,
  idle: @Composable () -> Unit = {},
  success: @Composable (T) -> Unit
) {
  // we only wanna animate if the resource type has changed
  val currentItem by remember(resource::class) {
    mutableStateOf(ResourceBoxItem(resource::class, resource))
  }

  SideEffect {
    currentItem.value = resource
  }

  AnimatedBox(
    current = currentItem,
    modifier = modifier,
    transition = transition
  ) { itemToRender ->
    when (val valueToRender = itemToRender.value) {
      is Idle -> idle()
      is Loading -> loading()
      is Success -> success(valueToRender.value)
      is Error -> error(valueToRender.error)
    }
  }
}

private class ResourceBoxItem<T>(
  val clazz: KClass<out Resource<T>>,
  value: Resource<T>
) {
  var value by mutableStateOf(value)

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as ResourceBoxItem<*>

    if (clazz != other.clazz) return false

    return true
  }

  override fun hashCode(): Int {
    return clazz.hashCode()
  }
}

object ResourceBoxDefaults {
  val transition = CrossFadeStackTransition(
    defaultAnimationSpec(
      150.milliseconds,
      easing = FastOutSlowInEasing
    )
  )
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
