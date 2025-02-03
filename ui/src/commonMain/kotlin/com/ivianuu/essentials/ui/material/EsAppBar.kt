/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.material

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
import androidx.compose.ui.graphics.*
import androidx.compose.ui.unit.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.compose.*
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.essentials.ui.systembars.*
import com.ivianuu.essentials.ui.util.*

@Composable fun EsAppBar(
  modifier: Modifier = Modifier,
  navigationIcon: @Composable () -> Unit = { NavigationIcon() },
  actions: @Composable RowScope.() -> Unit = {},
  expandedHeight: Dp = TopAppBarDefaults.TopAppBarExpandedHeight,
  windowInsets: WindowInsets = TopAppBarDefaults.windowInsets,
  colors: TopAppBarColors = TopAppBarDefaults.topAppBarColors(),
  scrollBehavior: TopAppBarScrollBehavior? = LocalAppBarScrollBehavior.current,
  applySystemBarStyle: Boolean = true,
  title: @Composable () -> Unit
) {
  val systemBarStyleModifier = if (applySystemBarStyle) {
    Modifier.systemBarStyle(
      bgColor = Color.Transparent,
      darkIcons = colors.containerColor.isLight
    )
  } else Modifier
  CenterAlignedTopAppBar(
    title = title,
    modifier = modifier.then(systemBarStyleModifier),
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
