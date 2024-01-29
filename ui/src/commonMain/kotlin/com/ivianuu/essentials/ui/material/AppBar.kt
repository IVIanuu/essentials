/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.material

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ivianuu.essentials.compose.LocalScope
import com.ivianuu.essentials.compose.action
import com.ivianuu.essentials.result.catch
import com.ivianuu.essentials.result.getOrNull
import com.ivianuu.essentials.ui.insets.InsetsPadding
import com.ivianuu.essentials.ui.navigation.navigator
import com.ivianuu.essentials.ui.navigation.pop
import com.ivianuu.essentials.ui.navigation.screen
import com.ivianuu.essentials.ui.systembars.systemBarStyle
import com.ivianuu.essentials.ui.util.isLight

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
    navigator?.backStack?.value?.indexOf(screen)?.let { it > 0 } == true
  }
  return if (!canGoBack) null
  else ({
    IconButton(onClick = action { navigator!!.pop(screen!!) }) {
      Icon(Icons.Default.ArrowBack, null)
    }
  })
}
