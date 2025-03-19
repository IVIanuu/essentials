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
import injekt.*

@Composable fun EsScaffold(
  modifier: Modifier = Modifier,
  topBar: (@Composable (@Provide TopAppBarScrollBehavior?) -> Unit)? = null,
  bottomBar: (@Composable () -> Unit)? = null,
  snackbarHost: (@Composable () -> Unit)? = null,
  floatingActionButton: (@Composable () -> Unit)? = null,
  floatingActionButtonPosition: FabPosition = FabPosition.End,
  containerColor: Color = MaterialTheme.colorScheme.background,
  contentColor: Color = guessingContentColorFor(containerColor),
  contentWindowInsets: WindowInsets = ScaffoldDefaults.contentWindowInsets,
  topBarScrollBehavior: TopAppBarScrollBehavior? = if (topBar == null) null
  else LocalAppBarScrollBehaviorProvider.current(),
  content: @Composable () -> Unit
) {
  Scaffold(
    modifier = modifier
      .then(
        if (topBarScrollBehavior == null) Modifier
        else Modifier.nestedScroll(topBarScrollBehavior.nestedScrollConnection)
      ),
    topBar = { topBar?.invoke(topBarScrollBehavior) },
    bottomBar = bottomBar ?: {},
    floatingActionButton = floatingActionButton ?: {},
    floatingActionButtonPosition = floatingActionButtonPosition,
    containerColor = containerColor,
    snackbarHost = snackbarHost ?: {},
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

val LocalContentPadding = compositionLocalOf {
  PaddingValues(0.dp)
}
