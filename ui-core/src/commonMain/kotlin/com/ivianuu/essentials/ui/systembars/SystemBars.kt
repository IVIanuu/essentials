/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.systembars

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import com.ivianuu.essentials.LoadingOrder
import com.ivianuu.essentials.ui.AppThemeDecorator
import com.ivianuu.essentials.ui.AppUiDecorator
import com.ivianuu.essentials.ui.insets.WindowInsetsProvider
import com.ivianuu.essentials.ui.util.isLight
import com.ivianuu.injekt.Provide

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

fun interface RootSystemBarsStyle : AppUiDecorator {
  @Provide companion object {
    @Provide val impl = RootSystemBarsStyle { content ->
      Surface(
        modifier = Modifier
          .fillMaxSize()
          .systemBarStyle(zIndex = -1),
        content = content
      )
    }

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

@Provide expect val systemBarManagerProvider: SystemBarManagerProvider

val LocalSystemBarManager = staticCompositionLocalOf<SystemBarManager> {
  error("No system bar manager provided")
}