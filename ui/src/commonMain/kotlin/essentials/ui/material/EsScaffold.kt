/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.ui.material

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.input.nestedscroll.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.unit.*

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
  val scrollBehavior = LocalAppBarScrollBehaviorProvider.current()

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
