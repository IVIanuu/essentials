/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.ui.material

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.*
import essentials.compose.*
import essentials.ui.navigation.*
import injekt.*

@Composable fun EsAppBar(
  modifier: Modifier = Modifier,
  navigationIcon: NavigationIcon? = inject,
  actions: (@Composable RowScope.() -> Unit)? = null,
  expandedHeight: Dp = TopAppBarDefaults.TopAppBarExpandedHeight,
  windowInsets: WindowInsets = TopAppBarDefaults.windowInsets,
  colors: TopAppBarColors = TopAppBarDefaults.topAppBarColors(),
  scrollBehavior: TopAppBarScrollBehavior? = LocalAppBarScrollBehavior.current,
  title: @Composable () -> Unit
) {
  CenterAlignedTopAppBar(
    title = title,
    modifier = modifier,
    navigationIcon = navigationIcon ?: {},
    actions = actions ?: {},
    expandedHeight = expandedHeight,
    windowInsets = windowInsets,
    colors = colors,
    scrollBehavior = scrollBehavior
  )
}

val LocalAppBarScrollBehaviorProvider = compositionLocalOf<@Composable () -> TopAppBarScrollBehavior?> {
  { TopAppBarDefaults.pinnedScrollBehavior() }
}

val LocalAppBarScrollBehavior = compositionLocalOf<TopAppBarScrollBehavior?> { null }

@Tag typealias NavigationIcon = @Composable () -> Unit
@Provide fun defaultNavigationIcon(navigator: Navigator, screen: Screen<*>): NavigationIcon = {
  BackPressButton(navigator, screen)
}

@Composable fun BackPressButton(navigator: Navigator = inject, screen: Screen<*> = inject) {
  val canGoBack = remember { navigator.backStack.indexOf(screen) > 0 }
  if (canGoBack)
    IconButton(onClick = action { navigator.pop(screen) }) {
      Icon(Icons.Default.ArrowBack, null)
    }
}
