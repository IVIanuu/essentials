/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.common

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.*
import com.ivianuu.essentials.resource.*

@Composable fun <T> ResourceBox(
  resource: Resource<T>,
  modifier: Modifier = Modifier,
  contentAlignment: Alignment = Alignment.TopStart,
  transitionSpec: AnimatedContentTransitionScope<Resource<T>>.() -> ContentTransform = {
    fadeIn() togetherWith fadeOut()
  },
  error: @Composable (Throwable) -> Unit = ResourceBoxDefaults.error,
  loading: @Composable () -> Unit = ResourceBoxDefaults.loading,
  idle: @Composable () -> Unit = ResourceBoxDefaults.idle,
  success: @Composable (T) -> Unit
) {
  AnimatedContent(
    targetState = resource,
    modifier = modifier,
    contentAlignment = contentAlignment,
    transitionSpec = transitionSpec,
    contentKey = { it::class }
  ) { itemToRender ->
    when (val value = itemToRender) {
      is Resource.Idle -> idle()
      is Resource.Loading -> loading()
      is Resource.Success -> success(value.value)
      is Resource.Error -> error(value.error)
    }
  }
}

object ResourceBoxDefaults {
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
