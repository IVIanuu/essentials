/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.material

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.unit.dp

val LocalContentPadding = compositionLocalOf {
  PaddingValues(0.dp)
}

@Composable fun EsScaffold(
  modifier: Modifier = Modifier,
  topBar: @Composable () -> Unit = {},
  bottomBar: @Composable () -> Unit = {},
  snackbarHost: @Composable () -> Unit = {},
  floatingActionButton: @Composable () -> Unit = {},
  floatingActionButtonPosition: FabPosition = FabPosition.End,
  containerColor: Color = MaterialTheme.colorScheme.background,
  contentColor: Color = contentColorFor(containerColor),
  contentWindowInsets: WindowInsets = ScaffoldDefaults.contentWindowInsets,
  content: @Composable () -> Unit
) {
  val scrollBehavior = LocalAppBarScrollBehaviorProvider
    .current.provide()

  Scaffold(
    modifier = modifier
      .then(
        if (scrollBehavior == null) Modifier
        else Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
      ),
    topBar = {
      CompositionLocalProvider(
        LocalAppBarScrollBehavior provides scrollBehavior
      ) { topBar.invoke() }
    },
    bottomBar = bottomBar,
    floatingActionButton = floatingActionButton,
    floatingActionButtonPosition = floatingActionButtonPosition,
    containerColor = containerColor,
    snackbarHost = snackbarHost,
    contentColor = contentColor,
    contentWindowInsets = contentWindowInsets
  ) { contentPadding ->
    println("content padding $contentPadding content window insets $contentWindowInsets")
    Box(
      modifier = Modifier
        .padding(
          start = contentPadding.calculateStartPadding(LocalLayoutDirection.current),
          end = contentPadding.calculateEndPadding(LocalLayoutDirection.current)
        )
    ) {
      CompositionLocalProvider(
        LocalContentPadding provides contentPadding,
        content = content
      )
    }
  }
}
