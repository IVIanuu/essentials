/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.material

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.unit.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.compose.*
import com.ivianuu.essentials.ui.insets.*
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.essentials.ui.systembars.*
import com.ivianuu.essentials.ui.util.*

enum class AppBarStyle { PRIMARY, SURFACE }

val LocalAppBarStyle = compositionLocalOf { AppBarStyle.PRIMARY }

@Composable fun AppBar(
  modifier: Modifier = Modifier,
  backgroundColor: Color = when (LocalAppBarStyle.current) {
    AppBarStyle.PRIMARY -> MaterialTheme.colors.primary
    AppBarStyle.SURFACE -> MaterialTheme.colors.surface
  },
  contentColor: Color = guessingContentColorFor(backgroundColor),
  elevation: Dp = DefaultAppBarElevation,
  applySystemBarStyle: Boolean = true,
  leading: (@Composable () -> Unit)? = autoTopAppBarLeadingIcon(),
  actions: (@Composable () -> Unit)? = null,
  bottomContent: (@Composable () -> Unit)? = null,
  title: (@Composable () -> Unit)? = null,
) {
  BaseTopAppBar(
    modifier = modifier,
    backgroundColor = backgroundColor,
    contentColor = contentColor,
    elevation = elevation,
    bottomContent = bottomContent,
    applySystemBarStyle = applySystemBarStyle
  ) {
    leading?.invoke()
    if (title != null) {
      val startPadding = if (leading != null) 16.dp else 0.dp
      val endPadding = if (actions != null) 16.dp else 0.dp
      Column(
        modifier = Modifier
          .padding(start = startPadding, end = endPadding)
          .weight(1f),
        verticalArrangement = Arrangement.Center
      ) {
        CompositionLocalProvider(
          LocalTextStyle provides MaterialTheme.typography.h6,
          LocalContentAlpha provides ContentAlpha.high,
          content = title
        )
      }
    }

    if (actions != null) {
      Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        actions()
      }
    }
  }
}

@Composable fun BaseTopAppBar(
  modifier: Modifier = Modifier,
  backgroundColor: Color = when (LocalAppBarStyle.current) {
    AppBarStyle.PRIMARY -> MaterialTheme.colors.primary
    AppBarStyle.SURFACE -> MaterialTheme.colors.surface
  },
  contentColor: Color = guessingContentColorFor(backgroundColor),
  elevation: Dp = DefaultAppBarElevation,
  applySystemBarStyle: Boolean = true,
  bottomContent: (@Composable () -> Unit)? = null,
  content: @Composable RowScope.() -> Unit,
) {
  val systemBarStyleModifier = if (applySystemBarStyle) {
    Modifier.systemBarStyle(
      bgColor = Color.Transparent,
      darkIcons = backgroundColor.isLight
    )
  } else Modifier
  Surface(
    color = backgroundColor,
    contentColor = contentColor,
    elevation = elevation,
    modifier = systemBarStyleModifier.then(modifier)
  ) {
    InsetsPadding(left = false, right = false, bottom = false) {
      Column {
        Row(
          modifier = Modifier
            .height(DefaultAppBarHeight)
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp),
          verticalAlignment = Alignment.CenterVertically,
          content = content
        )
        bottomContent?.invoke()
      }
    }
  }
}

private val DefaultAppBarHeight = 64.dp
val DefaultAppBarElevation = 0.dp

@Composable fun autoTopAppBarLeadingIcon(): (@Composable () -> Unit)? {
  val navigator = catch { LocalScope.current.navigator }.getOrNull()
  val screen = catch { LocalScope.current.screen }.getOrNull()
  val canGoBack = remember {
    navigator?.backStack?.indexOf(screen)?.let { it > 0 } == true
  }
  return if (!canGoBack) null
  else ({
    IconButton(onClick = action { navigator!!.pop(screen!!) }) {
      Icon(Icons.Default.ArrowBack, null)
    }
  })
}
