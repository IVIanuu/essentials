/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.systembars

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.ui.app.*
import com.ivianuu.essentials.ui.insets.*
import com.ivianuu.essentials.ui.util.*
import com.ivianuu.injekt.*

@Composable fun overlaySystemBarBgColor(color: Color) =
  if (color.isLight) Color.White.copy(alpha = 0.4f) else Color.Black.copy(alpha = 0.2f)

@Composable fun Modifier.systemBarStyle(
  bgColor: Color = overlaySystemBarBgColor(MaterialTheme.colors.surface),
  darkIcons: Boolean = bgColor.isLight,
  zIndex: Int = 0
): Modifier = composed {
  val style = remember { SystemBarStyle(bgColor, darkIcons, zIndex) }
  style.barColor = bgColor
  style.darkIcons = darkIcons
  style.zIndex = zIndex

  val systemBarManager = LocalSystemBarManager.current
  DisposableEffect(systemBarManager, style) {
    systemBarManager.registerStyle(style)
    onDispose { systemBarManager.unregisterStyle(style) }
  }

  onGloballyPositioned { style.bounds = it.boundsInWindow() }
}

@Provide class RootSystemBarsStyle : AppUiDecorator {
  @Composable override fun invoke(content: @Composable () -> Unit) {
    Surface(
      modifier = Modifier
        .fillMaxSize()
        .systemBarStyle(zIndex = -1),
      content = content
    )
  }

  @Provide companion object {
    @Provide val loadingOrder
      get() = LoadingOrder<RootSystemBarsStyle>()
        .after<AppThemeDecorator>()
        .after<SystemBarManagerProvider>()
  }
}

@Stable interface SystemBarManager {
  fun registerStyle(style: SystemBarStyle)

  fun unregisterStyle(style: SystemBarStyle)
}

@Stable class SystemBarStyle(barColor: Color, darkIcons: Boolean, zIndex: Int) {
  var barColor by mutableStateOf(barColor)
  var darkIcons by mutableStateOf(darkIcons)
  var bounds by mutableStateOf(Rect(0f, 0f, 0f, 0f))
  var zIndex by mutableStateOf(zIndex)
}

fun interface SystemBarManagerProvider : AppUiDecorator {
  @Provide companion object {
    @Provide val loadingOrder
      get() = LoadingOrder<SystemBarManagerProvider>()
        .after<WindowInsetsProvider>()
  }
}

val LocalSystemBarManager = staticCompositionLocalOf<SystemBarManager> {
  error("No system bar manager provided")
}
