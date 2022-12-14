/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.common

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.FabPosition
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.popup.PopupMenu
import com.ivianuu.essentials.ui.popup.PopupMenuButton

@Composable fun SimpleListScreen(
  title: String,
  popupMenuItems: List<PopupMenu.Item> = emptyList(),
  floatingActionButton: (@Composable () -> Unit)? = null,
  floatingActionButtonPosition: FabPosition = FabPosition.End,
  content: LazyListScope.() -> Unit
) {
  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text(title) },
        actions = if (popupMenuItems.isEmpty()) null else ({
          PopupMenuButton(items = popupMenuItems)
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
  popupMenuItems: List<PopupMenu.Item> = emptyList(),
  noinline floatingActionButton: (@Composable () -> Unit)? = null,
  floatingActionButtonPosition: FabPosition = FabPosition.End,
  noinline content: LazyListScope.() -> Unit
) {
  SimpleListScreen(
    stringResource(titleRes),
    popupMenuItems,
    floatingActionButton,
    floatingActionButtonPosition,
    content
  )
}
