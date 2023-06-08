/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.common

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.FabPosition
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.popup.PopupMenuButton

@Composable fun SimpleListScreen(
  title: String,
  modifier: Modifier = Modifier,
  popupMenuContent: (@Composable () -> Unit)? = null,
  floatingActionButton: (@Composable () -> Unit)? = null,
  floatingActionButtonPosition: FabPosition = FabPosition.End,
  content: LazyListScope.() -> Unit
) {
  Scaffold(
    modifier = modifier,
    topBar = {
      TopAppBar(
        title = { Text(title) },
        actions = if (popupMenuContent == null) null else ({
          PopupMenuButton(popupContent = popupMenuContent)
        })
      )
    },
    floatingActionButton = floatingActionButton,
    floatingActionButtonPosition = floatingActionButtonPosition
  ) {
    VerticalList(content = content)
  }
}

@Composable inline fun SimpleListScreen(
  titleRes: Int,
  modifier: Modifier = Modifier,
  noinline popupMenuContent: (@Composable () -> Unit)? = null,
  noinline floatingActionButton: (@Composable () -> Unit)? = null,
  floatingActionButtonPosition: FabPosition = FabPosition.End,
  noinline content: LazyListScope.() -> Unit
) {
  SimpleListScreen(
    stringResource(titleRes),
    modifier,
    popupMenuContent,
    floatingActionButton,
    floatingActionButtonPosition,
    content
  )
}
