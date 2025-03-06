/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.ui.material

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.*
import essentials.*
import essentials.compose.*
import essentials.ui.navigation.*

@Composable fun EsAppBar(
  modifier: Modifier = Modifier,
  navigationIcon: @Composable () -> Unit = { NavigationIcon() },
  actions: @Composable RowScope.() -> Unit = {},
  expandedHeight: Dp = TopAppBarDefaults.TopAppBarExpandedHeight,
  windowInsets: WindowInsets = TopAppBarDefaults.windowInsets,
  colors: TopAppBarColors = TopAppBarDefaults.topAppBarColors(),
  scrollBehavior: TopAppBarScrollBehavior? = LocalAppBarScrollBehavior.current,
  title: @Composable () -> Unit
) {
  CenterAlignedTopAppBar(
    title = title,
    modifier = modifier,
    navigationIcon = navigationIcon,
    actions = actions,
    expandedHeight = expandedHeight,
    windowInsets = windowInsets,
    colors = colors,
    scrollBehavior = scrollBehavior
  )
}

fun interface AppBarScrollBehaviorProvider {
  @Composable fun provide(): TopAppBarScrollBehavior?
}

val LocalAppBarScrollBehaviorProvider = compositionLocalOf {
  AppBarScrollBehaviorProvider { TopAppBarDefaults.pinnedScrollBehavior() }
}

val LocalAppBarScrollBehavior = compositionLocalOf<TopAppBarScrollBehavior?> { null }

@Composable fun NavigationIcon() {
  val navigator = catch { LocalScope.current.navigator }.getOrNull()
  val screen = catch { LocalScope.current.screen }.getOrNull()
  val canGoBack = remember {
    navigator?.backStack?.indexOf(screen)?.let { it > 0 } == true
  }

  if (canGoBack)
    IconButton(onClick = action { navigator!!.pop(screen!!) }) {
      Icon(Icons.Default.ArrowBack, null)
    }
}
